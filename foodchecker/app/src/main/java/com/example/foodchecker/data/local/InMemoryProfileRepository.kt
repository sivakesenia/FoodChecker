package com.example.foodchecker.data.local

import com.example.foodchecker.data.sample.SampleData
import com.example.foodchecker.domain.model.UserProfile
import com.example.foodchecker.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryProfileRepository : ProfileRepository {

    private val state = MutableStateFlow(SampleData.profile)

    override val profile: Flow<UserProfile> = state.asStateFlow()

    override suspend fun updateProfile(profile: UserProfile) {
        state.value = profile
    }
}
