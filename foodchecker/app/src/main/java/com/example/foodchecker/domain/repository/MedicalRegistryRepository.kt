package com.example.foodchecker.domain.repository

import com.example.foodchecker.domain.model.MedicalRegistry
import kotlinx.coroutines.flow.Flow

interface MedicalRegistryRepository {
    val registry: Flow<MedicalRegistry>
}
