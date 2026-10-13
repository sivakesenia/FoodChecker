package com.example.foodchecker.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodchecker.domain.model.UserProfile
import com.example.foodchecker.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = profileRepository.profile.first()
            _uiState.value = ProfileUiState(
                isLoading = false,
                name = profile.name,
                selectedAllergens = profile.criticalAllergens
            )
        }
    }

    fun onEvent(event: ProfileEvent) {
        // Пока профиль не загружен, правки игнорируются: иначе загрузка в init
        // перезаписала бы их, а сохранение было бы пропущено.
        if (_uiState.value.isLoading) return

        val newState = _uiState.updateAndGet { state ->
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
        saveProfile(newState)
    }

    private fun saveProfile(state: ProfileUiState) {
        viewModelScope.launch {
            profileRepository.updateProfile(
                UserProfile(name = state.name.trim(), criticalAllergens = state.selectedAllergens)
            )
        }
    }
}
