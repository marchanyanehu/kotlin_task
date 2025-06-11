package com.example.network.domain.usecase

import com.example.network.domain.model.CatCategory
import com.example.network.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting cat categories
 * Contains business logic for fetching and processing category data
 */
@Singleton
class GetCategoriesUseCase @Inject constructor(
    private val repository: CatRepository
) {
    
    /**
     * Execute use case to get categories
     * @param params Parameters for the request
     * @return Flow of Result containing list of categories
     */
    operator fun invoke(params: Params = Params()): Flow<Result<List<CatCategory>>> = flow {
        try {
            // Validate parameters
            val validatedParams = validateParams(params)
            
            // Fetch data from repository
            val result = repository.getCategories(
                limit = validatedParams.limit,
                page = validatedParams.page
            )
            
            // Apply business logic
            result.fold(
                onSuccess = { categories ->
                    val processedCategories = processCategories(categories, validatedParams)
                    emit(Result.success(processedCategories))
                },
                onFailure = { exception ->
                    emit(Result.failure(exception))
                }
            )
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    /**
     * Validate and sanitize input parameters
     */
    private fun validateParams(params: Params): Params {
        return params.copy(
            limit = params.limit?.coerceIn(1, 100),
            page = params.page?.coerceAtLeast(0)
        )
    }
    
    /**
     * Apply business logic to process categories
     */
    private fun processCategories(categories: List<CatCategory>, params: Params): List<CatCategory> {
        var processedCategories = categories
        
        // Filter by name if specified
        if (params.filterByName != null) {
            processedCategories = processedCategories.filter { category ->
                category.name.contains(params.filterByName, ignoreCase = true)
            }
        }
        
        // Sort by specified criteria
        processedCategories = when (params.sortBy) {
            SortBy.NAME -> processedCategories.sortedBy { it.name }
            SortBy.ID -> processedCategories.sortedBy { it.id }
            null -> processedCategories
        }
        
        return processedCategories
    }
    
    /**
     * Parameters for GetCategoriesUseCase
     */
    data class Params(
        val limit: Int? = null,
        val page: Int? = null,
        val filterByName: String? = null,
        val sortBy: SortBy? = null
    )
    
    /**
     * Sorting options for categories
     */
    enum class SortBy {
        NAME,
        ID
    }
} 