package com.example.network.data.network

import com.example.network.BuildConfig
import com.example.network.data.model.CatImageModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CatApiService {
    
    /**
     * Get random cat images
     * @param limit Number of images to return (1-10, default 1)
     * @param size Image size: small, med, full (default med)
     * @param mimeTypes Image types: jpg, png, gif (comma separated)
     * @param format Response format: json, src, xml (default json)
     * @param order Order: RANDOM, ASC, DESC (default RANDOM)
     * @param page Page number for pagination
     * @param categoryIds Category IDs to filter by (comma separated)
     * @param breedIds Breed IDs to filter by (comma separated)
     * @param apiKey API key for authentication
     */
    @GET("images/search")
    suspend fun getRandomCats(
        @Query("limit") limit: Int = 1,
        @Query("size") size: String? = null,
        @Query("mime_types") mimeTypes: String? = null,
        @Query("format") format: String? = null,
        @Query("order") order: String? = null,
        @Query("page") page: Int? = null,
        @Query("category_ids") categoryIds: String? = null,
        @Query("breed_ids") breedIds: String? = null,
        @Query("include_breeds") includeBreeds: Boolean? = null,
        @Query("include_categories") includeCategories: Boolean? = null,
        @Header("x-api-key") apiKey: String = BuildConfig.CAT_API_KEY
    ): List<CatImageModel>
    
    /**
     * Get a specific cat image by ID
     * @param imageId The image ID
     * @param apiKey API key for authentication
     */
    @GET("images/{image_id}")
    suspend fun getCatImageById(
        @Path("image_id") imageId: String
    ): CatImageModel
    
    /**
     * Get your uploaded images
     * @param limit Number of images to return (1-25, default 1)
     * @param mimeTypes Image types: jpg, png, gif (comma separated)
     * @param order Order: RANDOM, ASC, DESC (default RANDOM)
     * @param apiKey API key for authentication
     */
    @GET("images")
    suspend fun getUploadedImages(
        @Query("limit") limit: Int = 1,
        @Query("mime_types") mimeTypes: String? = null,
        @Query("order") order: String = "RANDOM",
        @Header("x-api-key") apiKey: String = BuildConfig.CAT_API_KEY
    ): List<CatImageModel>
    
    /**
     * Upload a cat image
     * @param file Image file (jpg, png, gif)
     * @param subId Optional sub ID for internal identification
     * @param breedIds Optional breed IDs
     * @param apiKey API key for authentication
     */
    @Multipart
    @POST("images/upload")
    suspend fun uploadCatImage(
        @Part file: MultipartBody.Part,
        @Part("sub_id") subId: String? = null,
        @Part("breed_ids") breedIds: String? = null,
        @Header("x-api-key") apiKey: String = BuildConfig.CAT_API_KEY
    ): Response<CatImageModel>
    
    /**
     * Delete a specific cat image
     * @param imageId The image ID to delete
     * @param apiKey API key for authentication
     */
    @DELETE("images/{image_id}")
    suspend fun deleteCatImage(
        @Path("image_id") imageId: String,
        @Header("x-api-key") apiKey: String = BuildConfig.CAT_API_KEY
    ): Response<Unit>
    
    /**
     * Get cat breeds information
     * @param attachBreed Attach breed information
     * @param page Page number for pagination
     * @param limit Number of breeds to return (1-25, default 10)
     * @param apiKey API key for authentication
     */
    @GET("breeds")
    suspend fun getBreeds(
        @Query("attach_breed") attachBreed: Int? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Header("x-api-key") apiKey: String = BuildConfig.CAT_API_KEY
    ): List<com.example.network.data.model.Breed>
    
    /**
     * Search for cat breeds by name
     * @param query Search query for breed name
     * @param apiKey API key for authentication
     */
    @GET("breeds/search")
    suspend fun searchBreeds(
        @Query("q") query: String
    ): List<com.example.network.data.model.Breed>
    
    /**
     * Get images by breed ID
     * @param breedId The breed ID
     * @param limit Number of images to return (1-10, default 1)
     * @param page Page number for pagination
     * @param size Image size: small, med, full (default med)
     * @param mimeTypes Image types: jpg, png, gif (comma separated)
     * @param format Response format: json, src, xml (default json)
     * @param order Order: RANDOM, ASC, DESC (default RANDOM)
     * @param includeBreeds Include breed information
     * @param includeCategories Include category information
     * @param apiKey API key for authentication
     */
    @GET("images/search")
    suspend fun getImagesByBreed(
        @Query("breed_ids") breedId: String,
        @Query("limit") limit: Int? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: String? = null,
        @Query("mime_types") mimeTypes: String? = null,
        @Query("format") format: String? = null,
        @Query("order") order: String? = null,
        @Query("include_breeds") includeBreeds: Boolean? = null,
        @Query("include_categories") includeCategories: Boolean? = null
    ): List<CatImageModel>
    
    /**
     * Get categories
     * @param limit Number of categories to return
     * @param page Page number for pagination
     * @param apiKey API key for authentication
     */
    @GET("categories")
    suspend fun getCategories(
        @Query("limit") limit: Int? = null,
        @Query("page") page: Int? = null,
        @Header("x-api-key") apiKey: String = BuildConfig.CAT_API_KEY
    ): List<com.example.network.data.model.Category>
    
    /**
     * Get images by category
     * @param categoryIds Category IDs (comma separated)
     * @param limit Number of images to return (1-10, default 1)
     * @param page Page number for pagination
     * @param size Image size: small, med, full (default med)
     * @param mimeTypes Image types: jpg, png, gif (comma separated)
     * @param format Response format: json, src, xml (default json)
     * @param order Order: RANDOM, ASC, DESC (default RANDOM)
     * @param includeBreeds Include breed information
     * @param includeCategories Include category information
     * @param apiKey API key for authentication
     */
    @GET("images/search")
    suspend fun getImagesByCategory(
        @Query("category_ids") categoryIds: String,
        @Query("limit") limit: Int? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: String? = null,
        @Query("mime_types") mimeTypes: String? = null,
        @Query("format") format: String? = null,
        @Query("order") order: String? = null,
        @Query("include_breeds") includeBreeds: Boolean? = null,
        @Query("include_categories") includeCategories: Boolean? = null
    ): List<CatImageModel>
}