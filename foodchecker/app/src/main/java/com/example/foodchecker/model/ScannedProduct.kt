package com.example.foodchecker.model

import java.time.LocalDate

data class ScannedProduct(
    val id: String,
    val barcode: String,
    val name: String,
    val brand: String,
    val scannedAt: LocalDate,
    val allergens: Set<Allergen>?
) {
    fun matches(query: String): Boolean =
        name.contains(query, ignoreCase = true) ||
            brand.contains(query, ignoreCase = true) ||
            barcode.contains(query)
}
