package com.example.foodchecker.ui.main

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodchecker.R
import com.example.foodchecker.data.MockProductRepository
import com.example.foodchecker.model.checkFor
import com.example.foodchecker.ui.components.LoadingPane
import com.example.foodchecker.ui.theme.FoodCheckerTheme
import kotlinx.coroutines.launch

@Composable
fun MainRoute(
    widthSizeClass: WindowWidthSizeClass,
    onOpenProfile: () -> Unit,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainScreen(
        state = uiState,
        widthSizeClass = widthSizeClass,
        onEvent = viewModel::onEvent,
        onOpenProfile = onOpenProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainUiState,
    widthSizeClass: WindowWidthSizeClass,
    onEvent: (MainEvent) -> Unit,
    onOpenProfile: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scanMessage = stringResource(R.string.scan_unavailable)
    val gridState = rememberLazyGridState()
    val isFabExpanded by remember {
        derivedStateOf { gridState.firstVisibleItemIndex == 0 }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onOpenProfile) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(R.string.profile_open)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { coroutineScope.launch { snackbarHostState.showSnackbar(scanMessage) } },
                expanded = isFabExpanded,
                icon = { Icon(Icons.Filled.QrCodeScanner, contentDescription = null) },
                text = { Text(stringResource(R.string.scan)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Crossfade(
            targetState = state.isLoading,
            modifier = Modifier.padding(innerPadding),
            label = "mainLoading"
        ) { isLoading ->
            when {
                isLoading -> LoadingPane()
                widthSizeClass == WindowWidthSizeClass.Expanded -> TwoPaneContent(
                    state = state,
                    gridState = gridState,
                    onEvent = onEvent,
                    onOpenProfile = onOpenProfile
                )
                else -> SinglePaneContent(
                    state = state,
                    gridState = gridState,
                    onEvent = onEvent,
                    onOpenProfile = onOpenProfile
                )
            }
        }
    }
}

@Composable
private fun SinglePaneContent(
    state: MainUiState,
    gridState: LazyGridState,
    onEvent: (MainEvent) -> Unit,
    onOpenProfile: () -> Unit
) {
    HistoryGrid(
        state = state,
        gridState = gridState,
        modifier = Modifier.fillMaxSize()
    ) {
        fullWidthItem(key = "profile") {
            ProfileCard(profile = state.profile, onClick = onOpenProfile)
        }
        fullWidthItem(key = "search") {
            SearchField(
                query = state.searchQuery,
                onQueryChanged = { onEvent(MainEvent.SearchQueryChanged(it)) },
                onClear = { onEvent(MainEvent.ClearSearch) }
            )
        }
    }
}

@Composable
private fun TwoPaneContent(
    state: MainUiState,
    gridState: LazyGridState,
    onEvent: (MainEvent) -> Unit,
    onOpenProfile: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileCard(profile = state.profile, onClick = onOpenProfile)
            SearchField(
                query = state.searchQuery,
                onQueryChanged = { onEvent(MainEvent.SearchQueryChanged(it)) },
                onClear = { onEvent(MainEvent.ClearSearch) }
            )
        }
        VerticalDivider()
        HistoryGrid(
            state = state,
            gridState = gridState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun HistoryGrid(
    state: MainUiState,
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
    header: LazyGridScope.() -> Unit = {}
) {
    val visibleChecks = state.visibleChecks

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        state = gridState,
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        header()
        fullWidthItem(key = "historyHeader") {
            HistoryHeader(count = state.checks.size)
        }
        if (visibleChecks.isEmpty()) {
            fullWidthItem(key = "historyEmpty") {
                HistoryEmptyState(
                    query = state.searchQuery,
                    hasHistory = state.checks.isNotEmpty(),
                    modifier = Modifier.animateItem()
                )
            }
        } else {
            items(items = visibleChecks, key = { it.product.id }) { check ->
                ProductHistoryItem(
                    check = check,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

private fun LazyGridScope.fullWidthItem(
    key: Any,
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(
        key = key,
        span = { GridItemSpan(maxLineSpan) },
        content = content
    )
}

private val previewState = MainUiState(
    isLoading = false,
    profile = MockProductRepository.sampleProfile,
    checks = MockProductRepository.sampleHistory.map {
        it.checkFor(MockProductRepository.sampleProfile)
    }
)

@Preview(name = "Телефон", showBackground = true, widthDp = 400, heightDp = 860)
@Composable
private fun MainScreenPhonePreview() {
    FoodCheckerTheme {
        MainScreen(
            state = previewState,
            widthSizeClass = WindowWidthSizeClass.Compact,
            onEvent = {},
            onOpenProfile = {}
        )
    }
}

@Preview(
    name = "Телефон, тёмная тема",
    showBackground = true,
    widthDp = 400,
    heightDp = 860,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MainScreenDarkPreview() {
    FoodCheckerTheme(darkTheme = true) {
        MainScreen(
            state = previewState.copy(searchQuery = "хлеб"),
            widthSizeClass = WindowWidthSizeClass.Compact,
            onEvent = {},
            onOpenProfile = {}
        )
    }
}

@Preview(name = "Планшет", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun MainScreenTabletPreview() {
    FoodCheckerTheme {
        MainScreen(
            state = previewState,
            widthSizeClass = WindowWidthSizeClass.Expanded,
            onEvent = {},
            onOpenProfile = {}
        )
    }
}
