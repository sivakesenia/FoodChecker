package com.example.foodchecker.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.foodchecker.FoodCheckerApplication
import com.example.foodchecker.data.ProductRepository
import com.example.foodchecker.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = repository.profile.first()
            _uiState.value = ProfileUiState(
                isLoading = false,
                name = profile.name,
                selectedAllergens = profile.criticalAllergens
            )
        }
    }

    fun onEvent(event: ProfileEvent) {
        _uiState.update { state ->
            when (event) {
                is ProfileEvent.NameChanged -> state.copy(name = event.name)
                is ProfileEvent.AllergenToggled -> state.copy(
                    selectedAllergens = if (event.allergen in state.selectedAllergens) {
                        state.selectedAllergens - event.allergen
                    } else {
                        state.selectedAllergens + event.allergen
                    }
                )
            }
        }
        saveProfile()
    }

    private fun saveProfile() {
        val state = _uiState.value
        if (state.isLoading) return
        viewModelScope.launch {
            repository.updateProfile(
                UserProfile(name = state.name.trim(), criticalAllergens = state.selectedAllergens)
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as FoodCheckerApplication
                ProfileViewModel(application.repository)
            }
        }
    }
}
