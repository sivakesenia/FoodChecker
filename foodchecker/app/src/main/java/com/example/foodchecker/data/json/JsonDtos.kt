package com.example.foodchecker.data.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class MedicalRegistryDto(
    val version: Int,
    val additives: List<AdditiveDto>,
    val allergens: List<AllergenMarkerDto>
)

@Serializable
data class AdditiveDto(
    val id: String,
    val marker: String,
    val name: String,
    val description: String,
    val risk: String,
    val synonyms: List<String> = emptyList()
)

@Serializable
data class AllergenMarkerDto(
    val id: String,
    val allergen: String,
    val description: String,
    val risk: String,
    val keywords: List<String>,
    val exclusions: List<String> = emptyList()
)

@Serializable
data class ProductCatalogDto(
    val products: List<ProductDto>
)

@Serializable
data class ProductDto(
    val barcode: String,
    val name: String,
    val brand: String,
    val ingredients: String? = null
)

@Serializable
data class DemoDataDto(
    val profile: DemoProfileDto,
    val history: List<DemoScanDto>
)

@Serializable
data class DemoProfileDto(
    val name: String,
    val allergens: List<String>
)

@Serializable
data class DemoScanDto(
    val barcode: String,
    @SerialName("scanned_at")
    @Serializable(with = LocalDateSerializer::class)
    val scannedAt: LocalDate
)
