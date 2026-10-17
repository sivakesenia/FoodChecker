package com.example.foodchecker.data.json

import com.example.foodchecker.domain.model.Allergen
import com.example.foodchecker.domain.model.Product
import com.example.foodchecker.domain.model.RiskLevel

fun ProductDto.toDomain(): Product = Product(
    barcode = barcode,
    name = name,
    brand = brand,
    ingredients = ingredients
)

fun String.toAllergenOrNull(): Allergen? = Allergen.entries.firstOrNull { it.name == this }

fun String.toRiskLevel(): RiskLevel = RiskLevel.entries.firstOrNull { it.name == this } ?: RiskLevel.MEDIUM
