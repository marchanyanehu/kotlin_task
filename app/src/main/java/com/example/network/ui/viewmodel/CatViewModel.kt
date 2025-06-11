package com.example.network.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.data.repository.NetworkException
import com.example.network.domain.usecase.GetBreedsUseCase
import com.example.network.domain.usecase.GetCategoriesUseCase
import com.example.network.domain.usecase.GetImagesByBreedUseCase
import com.example.network.domain.usecase.GetRandomCatsUseCase
import com.example.network.data.local.FavoriteCatsDataStore
import com.example.network.ui.state.CatUiEvent
import com.example.network.ui.state.CatUiState
import com.example.network.ui.state.ImageSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatViewModel @Inject constructor(
    private val getRandomCatsUseCase: GetRandomCatsUseCase,
    private val getBreedsUseCase: GetBreedsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getImagesByBreedUseCase: GetImagesByBreedUseCase,
    private val favoriteCatsDataStore: FavoriteCatsDataStore
) : ViewModel() {

    companion object {
        private const val TAG = "CatViewModel"
        private const val CATS_PER_PAGE = 10
        private const val SEARCH_DELAY_MS = 500L
    }

    private val _uiState = MutableStateFlow(CatUiState())
    val uiState: StateFlow<CatUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        Log.d(TAG, "CatViewModel initialized")
        observeFavorites()
        loadInitialData()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteCatsDataStore.favorites.collect { ids ->
                _uiState.value = _uiState.value.copy(favoriteCatIds = ids)
            }
        }
    }

    /**
     * Handle UI events
     */
    fun onEvent(event: CatUiEvent) {
        when (event) {
            is CatUiEvent.LoadRandomCats -> loadRandomCats()
            is CatUiEvent.LoadMoreCats -> loadMoreCats()
            is CatUiEvent.Refresh -> refresh()
            is CatUiEvent.SearchBreeds -> searchBreeds(event.query)
            is CatUiEvent.SelectBreed -> selectBreed(event.breed)
            is CatUiEvent.SelectCategory -> selectCategory(event.category)
            is CatUiEvent.ToggleFavorite -> toggleFavorite(event.catId)
            is CatUiEvent.ChangeImageSize -> changeImageSize(event.size)
            is CatUiEvent.ToggleShowOnlyWithBreeds -> toggleShowOnlyWithBreeds(event.show)
            is CatUiEvent.ClearError -> clearError()
            is CatUiEvent.LoadBreeds -> loadBreeds()
            is CatUiEvent.LoadCategories -> loadCategories()
        }
    }

    /**
     * Load initial data (cats, breeds, categories)
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            Log.d(TAG, "Loading initial data")
            loadRandomCats()
            loadBreeds()
            loadCategories()
        }
    }

    /**
     * Load random cat images
     */
    private fun loadRandomCats(resetList: Boolean = true) {
        viewModelScope.launch {
            if (resetList) {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    currentPage = 0
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            }

            val currentState = _uiState.value
            Log.d(TAG, "Loading random cats, reset: $resetList, filters: breed=${currentState.selectedBreed?.name}, category=${currentState.selectedCategory?.name}, onlyWithBreeds=${currentState.showOnlyWithBreeds}")
            val breedIds = currentState.selectedBreed?.id
            val categoryIds = currentState.selectedCategory?.id?.toString()
            
            // Use deterministic order when filters are applied to ensure consistent pagination
            val hasFilters = breedIds != null || categoryIds != null || currentState.showOnlyWithBreeds
            val order = if (hasFilters) "ASC" else "RANDOM"
            
            val params = GetRandomCatsUseCase.Params(
                limit = CATS_PER_PAGE,
                size = currentState.imageSize.apiValue,
                page = currentState.currentPage,
                breedIds = breedIds,
                categoryIds = categoryIds,
                order = order,
                preferBreedsWithInfo = currentState.showOnlyWithBreeds,
                minWidth = if (currentState.imageSize == ImageSize.LARGE) 800 else null,
                minHeight = if (currentState.imageSize == ImageSize.LARGE) 600 else null,
                includeBreeds = true, // Always include breed information
                includeCategories = true // Always include category information
            )
            
            getRandomCatsUseCase(params)
                .catch { exception ->
                    Log.e(TAG, "Failed to load cats", exception)
                    val errorMessage = getErrorMessage(exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = errorMessage
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { newCats ->
                            Log.d(TAG, "Successfully loaded ${newCats.size} cats")
                            val updatedCats = if (resetList) {
                                newCats
                            } else {
                                currentState.cats + newCats
                            }
                            
                            // Determine if there's more data available
                            // When filters are applied, we might get fewer results even if more data exists
                            val hasMoreData = if (currentState.selectedBreed != null || 
                                                 currentState.selectedCategory != null || 
                                                 currentState.showOnlyWithBreeds) {
                                // For filtered results, be more conservative about pagination
                                newCats.size >= CATS_PER_PAGE / 2
                            } else {
                                // For unfiltered results, use the original logic
                                newCats.size == CATS_PER_PAGE
                            }
                            
                            _uiState.value = _uiState.value.copy(
                                cats = updatedCats,
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = null,
                                hasMoreData = hasMoreData,
                                currentPage = currentState.currentPage + 1,
                                totalCatsLoaded = updatedCats.size,
                                lastRefreshTime = System.currentTimeMillis()
                            )
                        },
                        onFailure = { exception ->
                            Log.e(TAG, "Failed to load cats", exception)
                            val errorMessage = getErrorMessage(exception)
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = errorMessage
                            )
                        }
                    )
                }
        }
    }

    /**
     * Load more cats for pagination
     */
    private fun loadMoreCats() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMoreData) {
            Log.d(TAG, "Cannot load more cats: loading=${currentState.isLoadingMore}, hasMore=${currentState.hasMoreData}")
            return
        }
        
        Log.d(TAG, "Loading more cats")
        loadRandomCats(resetList = false)
    }

    /**
     * Refresh the cat list
     */
    private fun refresh() {
        Log.d(TAG, "Refreshing cat list")
        _uiState.value = _uiState.value.copy(
            currentPage = 0,
            hasMoreData = true
        )
        loadRandomCats(resetList = true)
    }

    /**
     * Load cat breeds
     */
    private fun loadBreeds() {
        viewModelScope.launch {
            Log.d(TAG, "Loading breeds")
            val params = GetBreedsUseCase.Params(
                limit = 50,
                sortBy = GetBreedsUseCase.SortBy.NAME
            )
            
            getBreedsUseCase(params)
                .catch { exception ->
                    Log.e(TAG, "Failed to load breeds", exception)
                    // Don't show error for breeds loading failure
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { breeds ->
                            Log.d(TAG, "Successfully loaded ${breeds.size} breeds")
                            _uiState.value = _uiState.value.copy(breeds = breeds)
                        },
                        onFailure = { exception ->
                            Log.e(TAG, "Failed to load breeds", exception)
                            // Don't show error for breeds loading failure
                        }
                    )
                }
        }
    }

    /**
     * Load categories
     */
    private fun loadCategories() {
        viewModelScope.launch {
            Log.d(TAG, "Loading categories")
            val params = GetCategoriesUseCase.Params(
                sortBy = GetCategoriesUseCase.SortBy.NAME
            )
            
            getCategoriesUseCase(params)
                .catch { exception ->
                    Log.e(TAG, "Failed to load categories", exception)
                    // Don't show error for categories loading failure
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { categories ->
                            Log.d(TAG, "Successfully loaded ${categories.size} categories")
                            _uiState.value = _uiState.value.copy(categories = categories)
                        },
                        onFailure = { exception ->
                            Log.e(TAG, "Failed to load categories", exception)
                            // Don't show error for categories loading failure
                        }
                    )
                }
        }
    }

    /**
     * Search breeds with debouncing
     */
    private fun searchBreeds(query: String) {
        Log.d(TAG, "Searching breeds with query: $query")
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            isSearching = query.isNotBlank()
        )

        searchJob?.cancel()
        
        if (query.isBlank()) {
            loadBreeds()
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY_MS)
            
            getBreedsUseCase.searchBreeds(query)
                .catch { exception ->
                    Log.e(TAG, "Failed to search breeds", exception)
                    _uiState.value = _uiState.value.copy(isSearching = false)
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { breeds ->
                            Log.d(TAG, "Found ${breeds.size} breeds for query: $query")
                            _uiState.value = _uiState.value.copy(
                                breeds = breeds,
                                isSearching = false
                            )
                        },
                        onFailure = { exception ->
                            Log.e(TAG, "Failed to search breeds", exception)
                            _uiState.value = _uiState.value.copy(isSearching = false)
                        }
                    )
                }
        }
    }

    /**
     * Select a breed for filtering
     */
    private fun selectBreed(breed: com.example.network.domain.model.CatBreed?) {
        Log.d(TAG, "Selected breed: ${breed?.name}")
        _uiState.value = _uiState.value.copy(
            selectedBreed = breed,
            // Don't clear category - allow multiple filters
            currentPage = 0,
            hasMoreData = true
        )
        loadRandomCats(resetList = true)
    }

    /**
     * Select a category for filtering
     */
    private fun selectCategory(category: com.example.network.domain.model.CatCategory?) {
        Log.d(TAG, "Selected category: ${category?.name}")
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            // Don't clear breed - allow multiple filters
            currentPage = 0,
            hasMoreData = true
        )
        loadRandomCats(resetList = true)
    }

    /**
     * Toggle favorite status of a cat
     */
    private fun toggleFavorite(catId: String) {
        Log.d(TAG, "Toggling favorite for cat: $catId")
        viewModelScope.launch {
            favoriteCatsDataStore.toggleFavorite(catId)
        }
    }

    /**
     * Change image size preference
     */
    private fun changeImageSize(size: ImageSize) {
        Log.d(TAG, "Changing image size to: ${size.displayName}")
        _uiState.value = _uiState.value.copy(
            imageSize = size,
            currentPage = 0,
            hasMoreData = true
        )
        loadRandomCats(resetList = true)
    }

    /**
     * Toggle show only cats with breed information
     */
    private fun toggleShowOnlyWithBreeds(show: Boolean) {
        Log.d(TAG, "Toggle show only with breeds: $show")
        _uiState.value = _uiState.value.copy(
            showOnlyWithBreeds = show,
            currentPage = 0,
            hasMoreData = true
        )
        loadRandomCats(resetList = true)
    }

    /**
     * Clear error message
     */
    private fun clearError() {
        Log.d(TAG, "Clearing error message")
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Get user-friendly error message
     */
    private fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is NetworkException.HttpError -> {
                when (exception.code) {
                    401 -> "Invalid API key. Please check your configuration."
                    403 -> "Access forbidden. Check your API permissions."
                    404 -> "Requested resource not found."
                    429 -> "Too many requests. Please try again later."
                    in 500..599 -> "Server error. Please try again later."
                    else -> "HTTP error: ${exception.code}"
                }
            }
            is NetworkException.NetworkError -> "Network error. Please check your internet connection."
            is NetworkException.UnknownError -> "An unexpected error occurred. Please try again."
            else -> exception.message ?: "Unknown error occurred"
        }
    }

    /**
     * Check if a cat is in favorites
     */
    fun isFavorite(catId: String): Boolean {
        return _uiState.value.favoriteCatIds.contains(catId)
    }

    /**
     * Get current filter summary
     */
    fun getFilterSummary(): String {
        val state = _uiState.value
        val filters = mutableListOf<String>()
        
        state.selectedBreed?.let { filters.add("Breed: ${it.name}") }
        state.selectedCategory?.let { filters.add("Category: ${it.name}") }
        if (state.showOnlyWithBreeds) filters.add("With descriptions")
        filters.add("Size: ${state.imageSize.displayName}")
        
        return if (filters.isEmpty()) "No filters" else filters.joinToString(" • ")
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        Log.d(TAG, "CatViewModel cleared")
    }
}