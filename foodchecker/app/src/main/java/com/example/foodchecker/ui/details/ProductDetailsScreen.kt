package com.example.foodchecker.ui.details

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodchecker.R
import com.example.foodchecker.domain.model.ProductDetails
import com.example.foodchecker.ui.components.LoadingPane
import com.example.foodchecker.ui.components.MessagePane
import com.example.foodchecker.ui.preview.PreviewData
import com.example.foodchecker.ui.theme.FoodCheckerTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ProductDetailsRoute(
    barcode: String,
    widthSizeClass: WindowWidthSizeClass,
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel = koinViewModel(parameters = { parametersOf(barcode) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProductDetailsScreen(
        state = uiState,
        widthSizeClass = widthSizeClass,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    state: ProductDetailsUiState,
    widthSizeClass: WindowWidthSizeClass,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details_title)) },
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
        AnimatedContent(
            targetState = state,
            contentKey = { it::class },
            transitionSpec = {
                (fadeIn() + slideInVertically { it / 12 }) togetherWith fadeOut()
            },
            modifier = Modifier.padding(innerPadding),
            label = "productDetails"
        ) { current ->
            when (current) {
                ProductDetailsUiState.Loading -> LoadingPane()
                ProductDetailsUiState.NotFound -> MessagePane(
                    icon = Icons.Filled.SearchOff,
                    title = stringResource(R.string.details_not_found_title),
                    text = stringResource(R.string.details_not_found_hint)
                )
                is ProductDetailsUiState.Content -> if (widthSizeClass == WindowWidthSizeClass.Expanded) {
                    TwoPaneDetails(details = current.details)
                } else {
                    SinglePaneDetails(details = current.details)
                }
            }
        }
    }
}

@Composable
private fun SinglePaneDetails(details: ProductDetails) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductHeader(product = details.product)
            VerdictBanner(analysis = details.analysis)
            CompositionSection(analysis = details.analysis)
            AllergensSection(findings = details.analysis.allergens)
            AdditivesSection(
                additives = details.analysis.additives,
                unknownCodes = details.analysis.unknownAdditiveCodes
            )
        }
    }
}

@Composable
private fun TwoPaneDetails(details: ProductDetails) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductHeader(product = details.product)
            VerdictBanner(analysis = details.analysis)
            CompositionSection(analysis = details.analysis)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AllergensSection(findings = details.analysis.allergens)
            AdditivesSection(
                additives = details.analysis.additives,
                unknownCodes = details.analysis.unknownAdditiveCodes
            )
        }
    }
}

@Preview(name = "Разбор состава", showBackground = true, widthDp = 400, heightDp = 1400)
@Composable
private fun ProductDetailsPreview() {
    FoodCheckerTheme {
        ProductDetailsScreen(
            state = ProductDetailsUiState.Content(PreviewData.details(PreviewData.SAUSAGE_BARCODE)),
            widthSizeClass = WindowWidthSizeClass.Compact,
            onBack = {}
        )
    }
}

@Preview(
    name = "Разбор состава, тёмная тема",
    showBackground = true,
    widthDp = 400,
    heightDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ProductDetailsDarkPreview() {
    FoodCheckerTheme(darkTheme = true) {
        ProductDetailsScreen(
            state = ProductDetailsUiState.Content(PreviewData.details(PreviewData.MAYONNAISE_BARCODE)),
            widthSizeClass = WindowWidthSizeClass.Compact,
            onBack = {}
        )
    }
}

@Preview(name = "Разбор состава, планшет", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun ProductDetailsTabletPreview() {
    FoodCheckerTheme {
        ProductDetailsScreen(
            state = ProductDetailsUiState.Content(PreviewData.details(PreviewData.NUT_BAR_BARCODE)),
            widthSizeClass = WindowWidthSizeClass.Expanded,
            onBack = {}
        )
    }
}
