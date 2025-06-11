package com.example.network.domain.usecase

import com.example.network.domain.model.CatBreed
import com.example.network.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting cat breeds
 * Contains business logic for fetching and processing breed data
 */
@Singleton
class GetBreedsUseCase @Inject constructor(
    private val repository: CatRepository
) {
    
    /**
     * Execute use case to get breeds
     * @param params Parameters for the request
     * @return Flow of Result containing list of breeds
     */
    operator fun invoke(params: Params = Params()): Flow<Result<List<CatBreed>>> = flow {
        try {
            // Validate parameters
            val validatedParams = validateParams(params)
            
            // Fetch data from repository
            val result = repository.getBreeds(
                attachBreed = validatedParams.attachBreed,
                page = validatedParams.page,
                limit = validatedParams.limit
            )
            
            // Apply business logic
            result.fold(
                onSuccess = { breeds ->
                    val processedBreeds = processBreeds(breeds, validatedParams)
                    emit(Result.success(processedBreeds))
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
     * Search breeds by query
     */
    fun searchBreeds(query: String): Flow<Result<List<CatBreed>>> = flow {
        try {
            if (query.isBlank()) {
                emit(Result.success(emptyList()))
                return@flow
            }
            
            val result = repository.searchBreeds(query.trim())
            
            result.fold(
                onSuccess = { breeds ->
                    // Sort by relevance (exact matches first, then partial matches)
                    val sortedBreeds = breeds.sortedWith(compareBy<CatBreed> { breed ->
                        when {
                            breed.name.equals(query, ignoreCase = true) -> 0
                            breed.name.startsWith(query, ignoreCase = true) -> 1
                            breed.name.contains(query, ignoreCase = true) -> 2
                            else -> 3
                        }
                    }.thenBy { it.name })
                    
                    emit(Result.success(sortedBreeds))
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
     * Apply business logic to process breeds
     */
    private fun processBreeds(breeds: List<CatBreed>, params: Params): List<CatBreed> {
        var processedBreeds = breeds
        
        // Filter by origin if specified
        if (params.filterByOrigin != null) {
            processedBreeds = processedBreeds.filter { breed ->
                breed.origin?.contains(params.filterByOrigin, ignoreCase = true) == true
            }
        }
        
        // Filter by temperament if specified
        if (params.filterByTemperament != null) {
            processedBreeds = processedBreeds.filter { breed ->
                breed.temperament?.contains(params.filterByTemperament, ignoreCase = true) == true
            }
        }
        
        // Sort by specified criteria
        processedBreeds = when (params.sortBy) {
            SortBy.NAME -> processedBreeds.sortedBy { it.name }
            SortBy.ORIGIN -> processedBreeds.sortedBy { it.origin ?: "" }
            SortBy.AFFECTION_LEVEL -> processedBreeds.sortedByDescending { it.affectionLevel ?: 0 }
            SortBy.ENERGY_LEVEL -> processedBreeds.sortedByDescending { it.energyLevel ?: 0 }
            SortBy.INTELLIGENCE -> processedBreeds.sortedByDescending { it.intelligence ?: 0 }
            null -> processedBreeds
        }
        
        return processedBreeds
    }
    
    /**
     * Parameters for GetBreedsUseCase
     */
    data class Params(
        val attachBreed: Int? = null,
        val page: Int? = null,
        val limit: Int? = null,
        val filterByOrigin: String? = null,
        val filterByTemperament: String? = null,
        val sortBy: SortBy? = null
    )
    
    /**
     * Sorting options for breeds
     */
    enum class SortBy {
        NAME,
        ORIGIN,
        AFFECTION_LEVEL,
        ENERGY_LEVEL,
        INTELLIGENCE
    }
} 