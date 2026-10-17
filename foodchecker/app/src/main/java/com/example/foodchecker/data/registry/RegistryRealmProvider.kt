package com.example.foodchecker.data.registry

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Лениво открывает Realm с медицинским реестром.
 *
 * Realm.open() читает файл с диска, поэтому выполняется на IO-диспетчере при первом
 * обращении, а не в момент, когда Koin создаёт зависимости (это происходит на главном потоке).
 * Экземпляр один на всё приложение.
 */
class RegistryRealmProvider(
    private val ioDispatcher: CoroutineDispatcher
) {

    private val mutex = Mutex()

    @Volatile
    private var realm: Realm? = null

    suspend fun get(): Realm = realm ?: mutex.withLock {
        realm ?: withContext(ioDispatcher) { Realm.open(configuration()) }
            .also { realm = it }
    }

    private fun configuration(): RealmConfiguration =
        RealmConfiguration.Builder(
            schema = setOf(AdditiveObject::class, AllergenMarkerObject::class)
        )
            .name(REALM_NAME)
            .schemaVersion(SCHEMA_VERSION)
            // Реестр — справочник, который заново импортируется из JSON,
            // поэтому при изменении схемы его безопасно пересоздать.
            .deleteRealmIfMigrationNeeded()
            .build()

    private companion object {
        const val REALM_NAME = "medical_registry.realm"
        const val SCHEMA_VERSION = 1L
    }
}
