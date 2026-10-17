package com.example.foodchecker.data.registry

import com.example.foodchecker.data.init.AppDataInitializer
import com.example.foodchecker.domain.model.Additive
import com.example.foodchecker.domain.model.AllergenMarker
import com.example.foodchecker.domain.model.MedicalRegistry
import com.example.foodchecker.domain.repository.MedicalRegistryRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RealmMedicalRegistryRepository(
    private val realmProvider: RegistryRealmProvider,
    private val initializer: AppDataInitializer
) : MedicalRegistryRepository {

    override val registry: Flow<MedicalRegistry> = flow {
        initializer.ensureInitialized()
        val realm = realmProvider.get()
        emitAll(
            combine(additives(realm), allergenMarkers(realm)) { additives, markers ->
                MedicalRegistry(additives = additives, allergenMarkers = markers)
            }
        )
    }.distinctUntilChanged()

    private fun additives(realm: Realm): Flow<List<Additive>> = realm
        .query<AdditiveObject>()
        .sort("marker", Sort.ASCENDING)
        .asFlow()
        .map { change -> change.list.map { it.toDomain() } }

    private fun allergenMarkers(realm: Realm): Flow<List<AllergenMarker>> = realm
        .query<AllergenMarkerObject>()
        .asFlow()
        .map { change -> change.list.mapNotNull { it.toDomainOrNull() } }
}
