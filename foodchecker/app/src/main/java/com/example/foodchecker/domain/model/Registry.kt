package com.example.foodchecker.domain.model

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

data class Additive(
    val id: String,
    val marker: String,
    val name: String,
    val description: String,
    val risk: RiskLevel,
    val synonyms: List<String> = emptyList()
)

data class AllergenMarker(
    val id: String,
    val allergen: Allergen,
    val keywords: List<String>,
    val description: String,
    val risk: RiskLevel = RiskLevel.HIGH,
    val exclusions: List<String> = emptyList()
)

data class MedicalRegistry(
    val additives: List<Additive>,
    val allergenMarkers: List<AllergenMarker>
) {
    private val additivesByMarker: Map<String, Additive> = additives.associateBy { it.marker.lowercase() }

    val isEmpty: Boolean
        get() = additives.isEmpty() && allergenMarkers.isEmpty()

    fun findAdditive(marker: String): Additive? {
        val key = marker.lowercase()
        return additivesByMarker[key] ?: additivesByMarker[key.trimEnd { it.isLetter() }]
    }

    companion object {
        val Empty = MedicalRegistry(additives = emptyList(), allergenMarkers = emptyList())
    }
}
