package com.example.foodchecker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.foodchecker.FoodCheckerApplication
import com.example.foodchecker.data.ProductRepository
import com.example.foodchecker.model.checkFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(repository.profile, repository.history) { profile, history ->
                profile to history.map { it.checkFor(profile) }
            }.collect { (profile, checks) ->
                _uiState.update {
                    it.copy(isLoading = false, profile = profile, checks = checks)
                }
            }
        }
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.SearchQueryChanged -> _uiState.update { it.copy(searchQuery = event.query) }
            MainEvent.ClearSearch -> _uiState.update { it.copy(searchQuery = "") }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as FoodCheckerApplication
                MainViewModel(application.repository)
            }
        }
    }
}
