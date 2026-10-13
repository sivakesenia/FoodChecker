package com.example.foodchecker.domain.model

import java.time.LocalDate

data class Product(
    val barcode: String,
    val name: String,
    val brand: String,
    val ingredients: String?
)

data class ScannedProduct(
    val barcode: String,
    val name: String,
    val brand: String,
    val ingredients: String?,
    val scannedAt: LocalDate
) {
    fun matches(query: String): Boolean =
        name.contains(query, ignoreCase = true) ||
            brand.contains(query, ignoreCase = true) ||
            barcode.contains(query)
}

sealed interface ScanResult {
    data class Found(val product: ScannedProduct) : ScanResult
    data class NotFound(val barcode: String) : ScanResult
}
