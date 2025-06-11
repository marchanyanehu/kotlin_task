package com.example.network.domain.usecase

import com.example.network.domain.model.Cat
import com.example.network.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting cat images by breed
 * Contains business logic for fetching and processing breed-specific cat data
 */
@Singleton
class GetImagesByBreedUseCase @Inject constructor(
    private val repository: CatRepository
) {
    
    /**
     * Execute use case to get images by breed
     * @param params Parameters for the request
     * @return Flow of Result containing list of cats
     */
    operator fun invoke(params: Params): Flow<Result<List<Cat>>> = flow {
        try {
            // Validate parameters
            val validatedParams = validateParams(params)
            
            // Fetch data from repository
            val result = repository.getImagesByBreed(
                breedId = validatedParams.breedId,
                limit = validatedParams.limit,
                page = validatedParams.page,
                size = validatedParams.size,
                mimeTypes = validatedParams.mimeTypes,
                format = validatedParams.format,
                order = validatedParams.order,
                includeBreeds = validatedParams.includeBreeds,
                includeCategories = validatedParams.includeCategories
            )
            
            // Apply business logic
            result.fold(
                onSuccess = { cats ->
                    val processedCats = processCats(cats, validatedParams)
                    emit(Result.success(processedCats))
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
        require(params.breedId.isNotBlank()) { "Breed ID cannot be blank" }
        
        return params.copy(
            limit = params.limit?.coerceIn(1, 100),
            page = params.page?.coerceAtLeast(0)
        )
    }
    
    /**
     * Apply business logic to process cats
     */
    private fun processCats(cats: List<Cat>, params: Params): List<Cat> {
        var processedCats = cats
        
        // Filter out cats without images
        processedCats = processedCats.filter { it.imageUrl.isNotBlank() }
        
        // Filter by image dimensions if specified
        if (params.minWidth != null || params.minHeight != null) {
            processedCats = processedCats.filter { cat ->
                val widthOk = params.minWidth?.let { cat.width >= it } ?: true
                val heightOk = params.minHeight?.let { cat.height >= it } ?: true
                widthOk && heightOk
            }
        }
        
        // Ensure breed information is present if requested
        if (params.includeBreeds == true) {
            processedCats = processedCats.filter { it.breeds.isNotEmpty() }
        }
        
        // Sort by quality score (cats with more breed info first)
        if (params.prioritizeQuality) {
            processedCats = processedCats.sortedWith(compareByDescending<Cat> { cat ->
                var score = 0
                if (cat.breeds.isNotEmpty()) score += 10
                if (cat.breeds.any { it.description?.isNotBlank() == true }) score += 5
                if (cat.breeds.any { it.temperament?.isNotBlank() == true }) score += 3
                if (cat.categories.isNotEmpty()) score += 2
                score
            }.thenByDescending { it.width * it.height }) // Then by image size
        }
        
        return processedCats
    }
    
    /**
     * Parameters for GetImagesByBreedUseCase
     */
    data class Params(
        val breedId: String,
        val limit: Int? = null,
        val page: Int? = null,
        val size: String? = null,
        val mimeTypes: String? = null,
        val format: String? = null,
        val order: String? = null,
        val includeBreeds: Boolean? = null,
        val includeCategories: Boolean? = null,
        val minWidth: Int? = null,
        val minHeight: Int? = null,
        val prioritizeQuality: Boolean = false
    )
} 