package com.example.foodchecker.data.local

import com.example.foodchecker.data.local.db.HistoryRow
import com.example.foodchecker.data.local.db.ProductEntity
import com.example.foodchecker.data.local.db.ProfileAllergenEntity
import com.example.foodchecker.data.local.db.ProfileWithAllergens
import com.example.foodchecker.data.local.db.UserProfileEntity
import com.example.foodchecker.domain.model.Product
import com.example.foodchecker.domain.model.ScannedProduct
import com.example.foodchecker.domain.model.UserProfile

fun ProfileWithAllergens.toDomain(): UserProfile = UserProfile(
    name = profile.name,
    criticalAllergens = allergens.map { it.allergen }.toSet()
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(name = name)

fun UserProfile.toAllergenEntities(profileId: Long): List<ProfileAllergenEntity> =
    criticalAllergens.map { ProfileAllergenEntity(profileId = profileId, allergen = it) }

fun Product.toEntity(): ProductEntity = ProductEntity(
    barcode = barcode,
    name = name,
    brand = brand,
    ingredients = ingredients
)

fun HistoryRow.toDomain(): ScannedProduct = ScannedProduct(
    barcode = barcode,
    name = name,
    brand = brand,
    ingredients = ingredients,
    scannedAt = scannedAt,
    scanCount = scanCount
)
