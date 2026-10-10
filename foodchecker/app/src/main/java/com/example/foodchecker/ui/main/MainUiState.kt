package com.example.foodchecker.ui.main

import com.example.foodchecker.model.ProductCheck
import com.example.foodchecker.model.UserProfile

data class MainUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile = UserProfile(name = "", criticalAllergens = emptySet()),
    val checks: List<ProductCheck> = emptyList(),
    val searchQuery: String = ""
) {
    val visibleChecks: List<ProductCheck>
        get() {
            val query = searchQuery.trim()
            return if (query.isEmpty()) checks else checks.filter { it.product.matches(query) }
        }
}

sealed interface MainEvent {
    data class SearchQueryChanged(val query: String) : MainEvent
    data object ClearSearch : MainEvent
}
