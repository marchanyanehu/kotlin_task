package com.example.network.domain.usecase

import com.example.network.domain.model.Cat
import com.example.network.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting random cats
 * Contains business logic for fetching and processing cat data
 */
@Singleton
class GetRandomCatsUseCase @Inject constructor(
    private val repository: CatRepository
) {
    
    /**
     * Execute use case to get random cats
     * @param params Parameters for the request
     * @return Flow of Result containing list of cats
     */
    operator fun invoke(params: Params): Flow<Result<List<Cat>>> = flow {
        try {
            // Validate parameters
            val validatedParams = validateParams(params)
            
            // Fetch data from repository
            val result = repository.getRandomCats(
                limit = validatedParams.limit,
                size = validatedParams.size,
                mimeTypes = validatedParams.mimeTypes,
                format = validatedParams.format,
                order = validatedParams.order,
                page = validatedParams.page,
                categoryIds = validatedParams.categoryIds,
                breedIds = validatedParams.breedIds,
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
        return params.copy(
            limit = params.limit.coerceIn(1, 100), // Limit between 1 and 100
            page = if (params.page != null) params.page.coerceAtLeast(0) else null
        )
    }
    
    /**
     * Apply business logic to process cats
     */
    private fun processCats(cats: List<Cat>, params: Params): List<Cat> {
        var processedCats = cats
        val originalCount = cats.size
        
        // Filter out cats without images
        processedCats = processedCats.filter { it.imageUrl.isNotBlank() }
        
        // Filter by breed info if requested
        if (params.preferBreedsWithInfo) {
            val beforeBreedFilter = processedCats.size
            processedCats = processedCats.filter { cat ->
                cat.breeds.isNotEmpty() && cat.breeds.any { breed ->
                    !breed.description.isNullOrBlank() || 
                    !breed.temperament.isNullOrBlank() ||
                    !breed.origin.isNullOrBlank()
                }
            }
            android.util.Log.d("GetRandomCatsUseCase", "Breed info filter: $beforeBreedFilter -> ${processedCats.size} cats (with descriptions)")
        }
        
        // Apply additional filtering
        if (params.minWidth != null || params.minHeight != null) {
            val beforeSizeFilter = processedCats.size
            processedCats = processedCats.filter { cat ->
                val widthOk = params.minWidth?.let { cat.width >= it } ?: true
                val heightOk = params.minHeight?.let { cat.height >= it } ?: true
                widthOk && heightOk
            }
            android.util.Log.d("GetRandomCatsUseCase", "Size filter: $beforeSizeFilter -> ${processedCats.size} cats")
        }
        
        android.util.Log.d("GetRandomCatsUseCase", "Total filtering: $originalCount -> ${processedCats.size} cats")
        return processedCats
    }
    
    /**
     * Parameters for GetRandomCatsUseCase
     */
    data class Params(
        val limit: Int = 10,
        val size: String? = null,
        val mimeTypes: String? = null,
        val format: String? = null,
        val order: String? = null,
        val page: Int? = null,
        val categoryIds: String? = null,
        val breedIds: String? = null,
        val preferBreedsWithInfo: Boolean = false,
        val minWidth: Int? = null,
        val minHeight: Int? = null,
        val includeBreeds: Boolean? = null,
        val includeCategories: Boolean? = null
    )
} 