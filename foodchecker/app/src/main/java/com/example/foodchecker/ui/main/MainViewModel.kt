package com.example.foodchecker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodchecker.domain.usecase.ObserveHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val observeHistory: ObserveHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeHistory().collect { overview ->
                _uiState.update {
                    it.copy(isLoading = false, profile = overview.profile, checks = overview.checks)
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
}
