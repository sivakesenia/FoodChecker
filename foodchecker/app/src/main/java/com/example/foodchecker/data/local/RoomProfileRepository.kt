package com.example.foodchecker.data.local

import com.example.foodchecker.data.init.AppDataInitializer
import com.example.foodchecker.data.local.db.ProfileDao
import com.example.foodchecker.data.local.db.UserProfileEntity
import com.example.foodchecker.domain.model.UserProfile
import com.example.foodchecker.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class RoomProfileRepository(
    private val profileDao: ProfileDao,
    private val initializer: AppDataInitializer
) : ProfileRepository {

    override val profile: Flow<UserProfile> = profileDao
        .observeProfile(UserProfileEntity.DEFAULT_ID)
        .map { it?.toDomain() ?: EmptyProfile }
        .distinctUntilChanged()
        .onStart { initializer.ensureInitialized() }

    override suspend fun updateProfile(profile: UserProfile) {
        val entity = profile.toEntity()
        profileDao.saveProfile(entity, profile.toAllergenEntities(entity.id))
    }

    private companion object {
        val EmptyProfile = UserProfile(name = "", criticalAllergens = emptySet())
    }
}
