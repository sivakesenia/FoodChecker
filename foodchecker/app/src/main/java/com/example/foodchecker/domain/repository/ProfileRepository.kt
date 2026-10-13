package com.example.foodchecker.domain.repository

import com.example.foodchecker.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    val profile: Flow<UserProfile>
    suspend fun updateProfile(profile: UserProfile)
}
