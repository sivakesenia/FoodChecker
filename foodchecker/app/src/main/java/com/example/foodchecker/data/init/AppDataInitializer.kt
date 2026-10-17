package com.example.foodchecker.data.init

import androidx.room.withTransaction
import com.example.foodchecker.data.json.AssetJsonDataSource
import com.example.foodchecker.data.json.toAllergenOrNull
import com.example.foodchecker.data.json.toDomain
import com.example.foodchecker.data.local.db.FoodCheckerDatabase
import com.example.foodchecker.data.local.db.ProfileAllergenEntity
import com.example.foodchecker.data.local.db.UserProfileEntity
import com.example.foodchecker.data.local.toEntity
import com.example.foodchecker.data.preferences.AppSettingsDataSource
import com.example.foodchecker.data.registry.AdditiveObject
import com.example.foodchecker.data.registry.AllergenMarkerObject
import com.example.foodchecker.data.registry.RegistryRealmProvider
import com.example.foodchecker.data.registry.toRealmObject
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AppDataInitializer(
    private val assets: AssetJsonDataSource,
    private val settings: AppSettingsDataSource,
    private val realmProvider: RegistryRealmProvider,
    private val database: FoodCheckerDatabase,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val mutex = Mutex()

    @Volatile
    private var isInitialized = false

    suspend fun ensureInitialized() {
        if (isInitialized) return
        mutex.withLock {
            if (isInitialized) return
            withContext(ioDispatcher) {
                importMedicalRegistry()
                seedDemoData()
            }
            isInitialized = true
        }
    }

    private suspend fun importMedicalRegistry() {
        val registry = assets.readMedicalRegistry()
        val realm = realmProvider.get()
        val isRealmEmpty = realm.query<AdditiveObject>().count().find() == 0L
        if (!isRealmEmpty && settings.current().registryVersion >= registry.version) return

        realm.write {
            delete(query<AdditiveObject>().find())
            delete(query<AllergenMarkerObject>().find())
            registry.additives.forEach { copyToRealm(it.toRealmObject(), UpdatePolicy.ALL) }
            registry.allergens.forEach { copyToRealm(it.toRealmObject(), UpdatePolicy.ALL) }
        }
        settings.setRegistryVersion(registry.version)
    }

    /**
     * Заполняет Room демонстрационным профилем и историей при первом запуске.
     *
     * Флаг в DataStore — только быстрая проверка, чтобы не читать JSON при каждом запуске.
     * Решение «загружать или нет» принимается внутри транзакции Room по наличию профиля:
     * профиль и история пишутся в одной транзакции, поэтому если профиль есть, демо-данные
     * уже загружены целиком. Так повторный запуск после сбоя между записью в Room и
     * записью флага не задублирует историю и не перезапишет профиль пользователя.
     */
    private suspend fun seedDemoData() {
        if (settings.current().isDemoDataSeeded) return

        val demo = assets.readDemoData()
        val catalog = assets.readProductCatalog().associateBy { it.barcode }
        val profile = UserProfileEntity(name = demo.profile.name)
        val allergens = demo.profile.allergens
            .mapNotNull { it.toAllergenOrNull() }
            .map { ProfileAllergenEntity(profileId = profile.id, allergen = it) }

        database.withTransaction {
            val profileDao = database.profileDao()
            if (profileDao.hasProfile()) return@withTransaction

            profileDao.saveProfile(profile, allergens)
            demo.history
                .sortedBy { it.scannedAt }
                .forEach { scan ->
                    val product = catalog[scan.barcode] ?: return@forEach
                    database.historyDao().recordScan(product.toDomain().toEntity(), scan.scannedAt)
                }
        }
        settings.setDemoDataSeeded(true)
    }
}
