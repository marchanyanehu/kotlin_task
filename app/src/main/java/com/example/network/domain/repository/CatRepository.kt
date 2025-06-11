package com.example.network.domain.repository

import com.example.network.domain.model.Cat
import com.example.network.domain.model.CatBreed
import com.example.network.domain.model.CatCategory
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

/**
 * Domain repository interface for Cat operations
 * This interface defines the contract for data operations
 * Implementation will be in data layer
 */
interface CatRepository {
    
    /**
     * Get random cat images
     */
    suspend fun getRandomCats(
        limit: Int = 10,
        size: String? = null,
        mimeTypes: String? = null,
        format: String? = null,
        order: String? = null,
        page: Int? = null,
        categoryIds: String? = null,
        breedIds: String? = null,
        includeBreeds: Boolean? = null,
        includeCategories: Boolean? = null
    ): Result<List<Cat>>
    
    /**
     * Get cat image by ID
     */
    suspend fun getCatImageById(imageId: String): Result<Cat>
    
    /**
     * Upload cat image
     */
    suspend fun uploadCatImage(
        file: MultipartBody.Part,
        subId: String? = null,
        breedIds: String? = null
    ): Result<Cat>
    
    /**
     * Delete cat image
     */
    suspend fun deleteCatImage(imageId: String): Result<Unit>
    
    /**
     * Get all breeds
     */
    suspend fun getBreeds(
        attachBreed: Int? = null,
        page: Int? = null,
        limit: Int? = null
    ): Result<List<CatBreed>>
    
    /**
     * Search breeds by name
     */
    suspend fun searchBreeds(query: String): Result<List<CatBreed>>
    
    /**
     * Get images by breed
     */
    suspend fun getImagesByBreed(
        breedId: String,
        limit: Int? = null,
        page: Int? = null,
        size: String? = null,
        mimeTypes: String? = null,
        format: String? = null,
        order: String? = null,
        includeBreeds: Boolean? = null,
        includeCategories: Boolean? = null
    ): Result<List<Cat>>
    
    /**
     * Get all categories
     */
    suspend fun getCategories(
        limit: Int? = null,
        page: Int? = null
    ): Result<List<CatCategory>>
    
    /**
     * Get images by category
     */
    suspend fun getImagesByCategory(
        categoryId: Int,
        limit: Int? = null,
        page: Int? = null,
        size: String? = null,
        mimeTypes: String? = null,
        format: String? = null,
        order: String? = null,
        includeBreeds: Boolean? = null,
        includeCategories: Boolean? = null
    ): Result<List<Cat>>
    
    /**
     * Get random cats as Flow for reactive programming
     */
    fun getRandomCatsFlow(
        limit: Int = 10,
        size: String? = null,
        categoryIds: String? = null,
        breedIds: String? = null
    ): Flow<Result<List<Cat>>>
} 