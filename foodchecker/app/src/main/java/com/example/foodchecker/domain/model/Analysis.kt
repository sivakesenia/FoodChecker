package com.example.foodchecker.domain.model

enum class Verdict {
    SAFE,
    DANGEROUS,
    UNKNOWN
}

enum class ComponentRisk {
    NONE,
    LOW,
    MEDIUM,
    HIGH
}

data class IngredientToken(
    val text: String,
    val isSeparator: Boolean,
    val allergens: Set<Allergen> = emptySet(),
    val additives: List<Additive> = emptyList(),
    val unknownCodes: List<String> = emptyList(),
    val risk: ComponentRisk = ComponentRisk.NONE
)

data class AllergenFinding(
    val marker: AllergenMarker,
    val isCritical: Boolean
)

data class IngredientAnalysis(
    val tokens: List<IngredientToken>,
    val allergens: List<AllergenFinding>,
    val additives: List<Additive>,
    val unknownAdditiveCodes: List<String>,
    val verdict: Verdict
) {
    val hasIngredients: Boolean
        get() = tokens.isNotEmpty()

    val criticalAllergens: Set<Allergen>
        get() = allergens.filter { it.isCritical }.map { it.marker.allergen }.toSet()

    val harmfulAdditives: List<Additive>
        get() = additives.filter { it.risk == RiskLevel.HIGH }

    companion object {
        val Empty = IngredientAnalysis(
            tokens = emptyList(),
            allergens = emptyList(),
            additives = emptyList(),
            unknownAdditiveCodes = emptyList(),
            verdict = Verdict.UNKNOWN
        )
    }
}

data class ProductCheck(
    val product: ScannedProduct,
    val verdict: Verdict,
    val criticalAllergens: Set<Allergen>,
    val harmfulAdditives: List<Additive>
)

data class ProductDetails(
    val product: ScannedProduct,
    val analysis: IngredientAnalysis
)

data class HistoryOverview(
    val profile: UserProfile,
    val checks: List<ProductCheck>
)
