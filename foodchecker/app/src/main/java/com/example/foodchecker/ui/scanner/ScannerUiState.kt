package com.example.foodchecker.ui.scanner

enum class CameraPermission {
    UNKNOWN,
    GRANTED,
    DENIED
}

sealed interface ScanStatus {
    data object Idle : ScanStatus
    data class Searching(val barcode: String) : ScanStatus
    data class NotFound(val barcode: String) : ScanStatus
}

data class ScannerUiState(
    val cameraPermission: CameraPermission = CameraPermission.UNKNOWN,
    val permissionRequested: Boolean = false,
    val manualBarcode: String = "",
    val status: ScanStatus = ScanStatus.Idle,
    val openProductBarcode: String? = null
) {
    val isManualBarcodeValid: Boolean
        get() = manualBarcode.length in MIN_BARCODE_LENGTH..MAX_BARCODE_LENGTH

    val isCameraActive: Boolean
        get() = status == ScanStatus.Idle && openProductBarcode == null

    companion object {
        const val MIN_BARCODE_LENGTH = 8
        const val MAX_BARCODE_LENGTH = 14
    }
}

sealed interface ScannerEvent {
    data class PermissionChecked(val granted: Boolean) : ScannerEvent
    data object PermissionRequested : ScannerEvent
    data class BarcodeDetected(val barcode: String) : ScannerEvent
    data class ManualBarcodeChanged(val value: String) : ScannerEvent
    data object ManualSearch : ScannerEvent
    data object RetryScan : ScannerEvent
    data object ProductOpened : ScannerEvent
}
