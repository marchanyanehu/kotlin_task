package com.example.network.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.network.ui.state.CatUiEvent
import com.example.network.ui.viewmodel.CatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatListScreen(
    viewModel: CatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showFilterSheet by remember { mutableStateOf(false) }

    // Check if we need to load more items
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleIndex >= totalItems - 3 && uiState.hasMoreData && !uiState.isLoadingMore
        }
    }

    // Load more items when needed
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.onEvent(CatUiEvent.LoadMoreCats)
        }
    }

    // Show error messages in snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.onEvent(CatUiEvent.ClearError)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Cat API Demo")
                        if (uiState.totalCatsLoaded > 0) {
                            Text(
                                text = "${uiState.totalCatsLoaded} cats loaded",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showFilterSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Filters"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.onEvent(CatUiEvent.Refresh) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter summary
            if (uiState.selectedBreed != null || uiState.selectedCategory != null || uiState.showOnlyWithBreeds) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Filters: ${viewModel.getFilterSummary()}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Loading indicator for more items
            if (uiState.isLoadingMore) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            when {
                uiState.isLoading && uiState.cats.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading cats...")
                        }
                    }
                }

                uiState.cats.isEmpty() && !uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No cats found",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.onEvent(CatUiEvent.LoadRandomCats) }
                            ) {
                                Text("Load Cats")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        items(
                            items = uiState.cats,
                            key = { it.id }
                        ) { cat ->
                            CatCard(
                                cat = cat,
                                isFavorite = viewModel.isFavorite(cat.id),
                                onFavoriteClick = { 
                                    viewModel.onEvent(CatUiEvent.ToggleFavorite(cat.id))
                                }
                            )
                        }

                        // Loading indicator at the bottom
                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text("Loading more cats...")
                                    }
                                }
                            }
                        }

                        // End of list indicator
                        if (!uiState.hasMoreData && uiState.cats.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "You've seen all the cats! 🐱",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Filter Bottom Sheet
    FilterBottomSheet(
        isVisible = showFilterSheet,
        onDismiss = { showFilterSheet = false },
        breeds = uiState.breeds,
        categories = uiState.categories,
        selectedBreed = uiState.selectedBreed,
        selectedCategory = uiState.selectedCategory,
        selectedImageSize = uiState.imageSize,
        showOnlyWithBreeds = uiState.showOnlyWithBreeds,
        onBreedSelected = { breed ->
            viewModel.onEvent(CatUiEvent.SelectBreed(breed))
        },
        onCategorySelected = { category ->
            viewModel.onEvent(CatUiEvent.SelectCategory(category))
        },
        onImageSizeSelected = { size ->
            viewModel.onEvent(CatUiEvent.ChangeImageSize(size))
        },
        onShowOnlyWithBreedsChanged = { show ->
            viewModel.onEvent(CatUiEvent.ToggleShowOnlyWithBreeds(show))
        },
        onApplyFilters = {
            viewModel.onEvent(CatUiEvent.Refresh)
        },
        onClearFilters = {
            viewModel.onEvent(CatUiEvent.SelectBreed(null))
            viewModel.onEvent(CatUiEvent.SelectCategory(null))
            viewModel.onEvent(CatUiEvent.ToggleShowOnlyWithBreeds(false))
            viewModel.onEvent(CatUiEvent.ChangeImageSize(com.example.network.ui.state.ImageSize.MEDIUM))
            viewModel.onEvent(CatUiEvent.Refresh)
        }
    )
}

@Preview
@Composable
fun CatListScreenPreview() = CatListScreen()