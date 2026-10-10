package com.example.foodchecker.model

enum class Verdict {
    SAFE,
    DANGEROUS,
    UNKNOWN
}

data class ProductCheck(
    val product: ScannedProduct,
    val verdict: Verdict,
    val matchedAllergens: Set<Allergen>
)

fun ScannedProduct.checkFor(profile: UserProfile): ProductCheck {
    val contained = allergens ?: return ProductCheck(this, Verdict.UNKNOWN, emptySet())
    val matched = contained intersect profile.criticalAllergens
    val verdict = if (matched.isEmpty()) Verdict.SAFE else Verdict.DANGEROUS
    return ProductCheck(this, verdict, matched)
}
