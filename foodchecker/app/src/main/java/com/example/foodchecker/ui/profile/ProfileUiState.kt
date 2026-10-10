package com.example.foodchecker.ui.profile

import com.example.foodchecker.model.Allergen
import com.example.foodchecker.model.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val name: String = "",
    val selectedAllergens: Set<Allergen> = emptySet()
) {
    val isNameValid: Boolean
        get() = name.isNotBlank()

    val initials: String
        get() = UserProfile(name, selectedAllergens).initials
}

sealed interface ProfileEvent {
    data class NameChanged(val name: String) : ProfileEvent
    data class AllergenToggled(val allergen: Allergen) : ProfileEvent
}
