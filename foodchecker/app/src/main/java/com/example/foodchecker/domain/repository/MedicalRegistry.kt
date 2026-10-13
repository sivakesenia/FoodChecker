package com.example.foodchecker.domain.repository

import com.example.foodchecker.domain.model.Additive
import com.example.foodchecker.domain.model.AllergenMarker

interface MedicalRegistry {
    fun additives(): List<Additive>
    fun allergenMarkers(): List<AllergenMarker>
    fun findAdditive(marker: String): Additive?
}
