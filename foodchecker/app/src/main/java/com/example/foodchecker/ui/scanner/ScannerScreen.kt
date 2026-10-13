package com.example.foodchecker.ui.scanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodchecker.R
import com.example.foodchecker.ui.theme.FoodCheckerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScannerRoute(
    widthSizeClass: WindowWidthSizeClass,
    onBack: () -> Unit,
    onProductFound: (String) -> Unit,
    viewModel: ScannerViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onEvent(ScannerEvent.PermissionChecked(granted))
    }

    LifecycleResumeEffect(Unit) {
        viewModel.onEvent(ScannerEvent.PermissionChecked(context.hasCameraPermission()))
        onPauseOrDispose { }
    }

    LaunchedEffect(state.cameraPermission, state.permissionRequested) {
        if (state.cameraPermission == CameraPermission.DENIED && !state.permissionRequested) {
            viewModel.onEvent(ScannerEvent.PermissionRequested)
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(state.openProductBarcode) {
        val barcode = state.openProductBarcode ?: return@LaunchedEffect
        onProductFound(barcode)
        viewModel.onEvent(ScannerEvent.ProductOpened)
    }

    ScannerScreen(
        state = state,
        widthSizeClass = widthSizeClass,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
        onOpenSettings = { context.openAppSettings() }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScannerScreen(
    state: ScannerUiState,
    widthSizeClass: WindowWidthSizeClass,
    onEvent: (ScannerEvent) -> Unit,
    onBack: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scanner_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
            .imePadding()

        if (widthSizeClass == WindowWidthSizeClass.Compact) {
            Column(modifier = contentModifier) {
                CameraArea(
                    state = state,
                    onEvent = onEvent,
                    onRequestPermission = onRequestPermission,
                    onOpenSettings = onOpenSettings,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                ScannerPanel(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Row(modifier = contentModifier) {
                CameraArea(
                    state = state,
                    onEvent = onEvent,
                    onRequestPermission = onRequestPermission,
                    onOpenSettings = onOpenSettings,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                ScannerPanel(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier
                        .width(380.dp)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    }
}

@Composable
private fun CameraArea(
    state: ScannerUiState,
    onEvent: (ScannerEvent) -> Unit,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.inverseSurface),
        contentAlignment = Alignment.Center
    ) {
        when (state.cameraPermission) {
            CameraPermission.GRANTED -> {
                BarcodeCameraPreview(
                    onBarcodeDetected = { onEvent(ScannerEvent.BarcodeDetected(it)) },
                    modifier = Modifier.fillMaxSize()
                )
                ScannerOverlay(
                    isScanning = state.isCameraActive,
                    modifier = Modifier.fillMaxSize()
                )
            }
            CameraPermission.DENIED -> PermissionRequired(
                onRequestPermission = onRequestPermission,
                onOpenSettings = onOpenSettings,
                modifier = Modifier.padding(24.dp)
            )
            CameraPermission.UNKNOWN -> Unit
        }
    }
}

@Composable
private fun PermissionRequired(
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.NoPhotography,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.camera_permission_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.camera_permission_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRequestPermission) {
                Text(stringResource(R.string.camera_permission_grant))
            }
            OutlinedButton(onClick = onOpenSettings) {
                Text(stringResource(R.string.camera_permission_settings))
            }
        }
    }
}

@Composable
private fun ScannerPanel(
    state: ScannerUiState,
    onEvent: (ScannerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val isSearching = state.status is ScanStatus.Searching

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedContent(
                targetState = state.status,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "scanStatus"
            ) { status ->
                when (status) {
                    ScanStatus.Idle -> StatusRow(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        text = stringResource(R.string.scan_hint)
                    )
                    is ScanStatus.Searching -> StatusRow(
                        icon = { CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp) },
                        text = stringResource(R.string.scan_searching, status.barcode)
                    )
                    is ScanStatus.NotFound -> NotFoundCard(
                        barcode = status.barcode,
                        onRetry = { onEvent(ScannerEvent.RetryScan) }
                    )
                }
            }
            OutlinedTextField(
                value = state.manualBarcode,
                onValueChange = { onEvent(ScannerEvent.ManualBarcodeChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSearching,
                label = { Text(stringResource(R.string.scan_manual_label)) },
                supportingText = { Text(stringResource(R.string.scan_manual_hint)) },
                leadingIcon = { Icon(Icons.Filled.Pin, contentDescription = null) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onEvent(ScannerEvent.ManualSearch)
                        },
                        enabled = state.isManualBarcodeValid && !isSearching
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.scan_manual_search)
                        )
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        onEvent(ScannerEvent.ManualSearch)
                    }
                )
            )
        }
    }
}

@Composable
private fun StatusRow(
    icon: @Composable () -> Unit,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun NotFoundCard(
    barcode: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.SearchOff, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.scan_not_found_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = stringResource(R.string.scan_not_found_hint, barcode),
                style = MaterialTheme.typography.bodyMedium
            )
            FilledTonalButton(onClick = onRetry) {
                Text(stringResource(R.string.scan_retry))
            }
        }
    }
}

private fun Context.hasCameraPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

private fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

@Preview(name = "Сканер, товар не найден", showBackground = true, widthDp = 400, heightDp = 860)
@Composable
private fun ScannerNotFoundPreview() {
    FoodCheckerTheme {
        ScannerScreen(
            state = ScannerUiState(
                cameraPermission = CameraPermission.GRANTED,
                manualBarcode = "4600000000000",
                status = ScanStatus.NotFound("4600000000000")
            ),
            widthSizeClass = WindowWidthSizeClass.Compact,
            onEvent = {},
            onBack = {},
            onRequestPermission = {},
            onOpenSettings = {}
        )
    }
}

@Preview(
    name = "Сканер, нет разрешения",
    showBackground = true,
    widthDp = 400,
    heightDp = 860,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ScannerPermissionPreview() {
    FoodCheckerTheme(darkTheme = true) {
        ScannerScreen(
            state = ScannerUiState(cameraPermission = CameraPermission.DENIED, permissionRequested = true),
            widthSizeClass = WindowWidthSizeClass.Compact,
            onEvent = {},
            onBack = {},
            onRequestPermission = {},
            onOpenSettings = {}
        )
    }
}
