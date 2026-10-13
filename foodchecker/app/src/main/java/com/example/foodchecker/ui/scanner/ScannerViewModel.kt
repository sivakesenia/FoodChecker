package com.example.foodchecker.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodchecker.domain.model.ScanResult
import com.example.foodchecker.domain.usecase.ScanProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val scanProduct: ScanProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun onEvent(event: ScannerEvent) {
        when (event) {
            is ScannerEvent.PermissionChecked -> _uiState.update {
                it.copy(cameraPermission = if (event.granted) CameraPermission.GRANTED else CameraPermission.DENIED)
            }
            ScannerEvent.PermissionRequested -> _uiState.update { it.copy(permissionRequested = true) }
            is ScannerEvent.BarcodeDetected -> {
                if (_uiState.value.isCameraActive) search(event.barcode)
            }
            is ScannerEvent.ManualBarcodeChanged -> _uiState.update {
                it.copy(manualBarcode = event.value.filter(Char::isDigit).take(ScannerUiState.MAX_BARCODE_LENGTH))
            }
            ScannerEvent.ManualSearch -> {
                val state = _uiState.value
                if (state.isManualBarcodeValid && state.status !is ScanStatus.Searching) {
                    search(state.manualBarcode)
                }
            }
            ScannerEvent.RetryScan -> _uiState.update { it.copy(status = ScanStatus.Idle) }
            ScannerEvent.ProductOpened -> _uiState.update {
                it.copy(openProductBarcode = null, status = ScanStatus.Idle, manualBarcode = "")
            }
        }
    }

    private fun search(barcode: String) {
        _uiState.update { it.copy(status = ScanStatus.Searching(barcode)) }
        viewModelScope.launch {
            when (val result = scanProduct(barcode)) {
                is ScanResult.Found -> _uiState.update {
                    it.copy(status = ScanStatus.Idle, openProductBarcode = result.product.barcode)
                }
                is ScanResult.NotFound -> _uiState.update {
                    it.copy(status = ScanStatus.NotFound(result.barcode))
                }
            }
        }
    }
}
