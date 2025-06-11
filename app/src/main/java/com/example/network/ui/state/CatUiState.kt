package com.example.network.ui.state

import com.example.network.domain.model.Cat
import com.example.network.domain.model.CatBreed
import com.example.network.domain.model.CatCategory

data class CatUiState(
    // Основные данные
    val cats: List<Cat> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    
    // Пагинация
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val currentPage: Int = 0,
    
    // Фильтрация и поиск
    val breeds: List<CatBreed> = emptyList(),
    val categories: List<CatCategory> = emptyList(),
    val selectedBreed: CatBreed? = null,
    val selectedCategory: CatCategory? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    
    // Настройки отображения
    val imageSize: ImageSize = ImageSize.MEDIUM,
    val showOnlyWithBreeds: Boolean = false,
    
    // Избранное (локальное состояние)
    val favoriteCatIds: Set<String> = emptySet(),
    
    // Статистика
    val totalCatsLoaded: Int = 0,
    val lastRefreshTime: Long = 0L
)

enum class ImageSize(val apiValue: String, val displayName: String) {
    SMALL("small", "Small"),
    MEDIUM("med", "Medium"),
    LARGE("full", "Large")
}

// UI Events для ViewModel
sealed class CatUiEvent {
    object LoadRandomCats : CatUiEvent()
    object LoadMoreCats : CatUiEvent()
    object Refresh : CatUiEvent()
    data class SearchBreeds(val query: String) : CatUiEvent()
    data class SelectBreed(val breed: CatBreed?) : CatUiEvent()
    data class SelectCategory(val category: CatCategory?) : CatUiEvent()
    data class ToggleFavorite(val catId: String) : CatUiEvent()
    data class ChangeImageSize(val size: ImageSize) : CatUiEvent()
    data class ToggleShowOnlyWithBreeds(val show: Boolean) : CatUiEvent()
    object ClearError : CatUiEvent()
    object LoadBreeds : CatUiEvent()
    object LoadCategories : CatUiEvent()
}