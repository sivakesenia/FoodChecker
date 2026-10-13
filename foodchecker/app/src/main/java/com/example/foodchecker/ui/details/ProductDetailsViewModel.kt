package com.example.foodchecker.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodchecker.domain.model.ProductDetails
import com.example.foodchecker.domain.usecase.ObserveProductDetailsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface ProductDetailsUiState {
    data object Loading : ProductDetailsUiState
    data object NotFound : ProductDetailsUiState
    data class Content(val details: ProductDetails) : ProductDetailsUiState
}

class ProductDetailsViewModel(
    barcode: String,
    observeProductDetails: ObserveProductDetailsUseCase
) : ViewModel() {

    val uiState: StateFlow<ProductDetailsUiState> = observeProductDetails(barcode)
        .map { details ->
            if (details == null) ProductDetailsUiState.NotFound else ProductDetailsUiState.Content(details)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = ProductDetailsUiState.Loading
        )

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
