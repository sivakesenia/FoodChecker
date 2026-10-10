package com.example.foodchecker.data

import com.example.foodchecker.model.ScannedProduct
import com.example.foodchecker.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    val profile: Flow<UserProfile>
    val history: Flow<List<ScannedProduct>>
    suspend fun updateProfile(profile: UserProfile)
}
