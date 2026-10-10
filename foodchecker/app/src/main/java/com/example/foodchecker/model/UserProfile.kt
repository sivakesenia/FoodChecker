package com.example.foodchecker.model

data class UserProfile(
    val name: String,
    val criticalAllergens: Set<Allergen>
) {
    val initials: String
        get() = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
}
