package com.example.network.data.repository

import android.util.Log
import com.example.network.data.mapper.CatMapper.toDomain
import com.example.network.data.mapper.CatMapper.toDomainBreeds
import com.example.network.data.mapper.CatMapper.toDomainCategories
import com.example.network.data.network.CatApiService
import com.example.network.domain.model.Cat
import com.example.network.domain.model.CatBreed
import com.example.network.domain.model.CatCategory
import com.example.network.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CatRepository interface
 * This class handles data operations and converts between data and domain models
 */
@Singleton
class CatRepositoryImpl @Inject constructor(
    private val apiService: CatApiService
) : CatRepository {
    
    companion object {
        private const val TAG = "CatRepositoryImpl"
    }
    
    override suspend fun getRandomCats(
        limit: Int,
        size: String?,
        mimeTypes: String?,
        format: String?,
        order: String?,
        page: Int?,
        categoryIds: String?,
        breedIds: String?,
        includeBreeds: Boolean?,
        includeCategories: Boolean?
    ): Result<List<Cat>> {
        return try {
            Log.d(TAG, "Fetching $limit random cats")
            val cats = apiService.getRandomCats(
                limit = limit,
                size = size,
                mimeTypes = mimeTypes,
                format = format,
                order = order,
                page = page,
                categoryIds = categoryIds,
                breedIds = breedIds,
                includeBreeds = includeBreeds,
                includeCategories = includeCategories
            )
            Log.d(TAG, "Successfully fetched ${cats.size} cats")
            Result.success(cats.toDomain())
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while fetching random cats: ${e.code()} - ${e.message()}")
            Result.failure(NetworkException.HttpError(e.code(), e.message()))
        } catch (e: IOException) {
            Log.e(TAG, "Network error while fetching random cats", e)
            Result.failure(NetworkException.NetworkError(e.message ?: "Network error"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while fetching random cats", e)
            Result.failure(NetworkException.UnknownError(e.message ?: "Unknown error"))
        }
    }
    
    override suspend fun getCatImageById(imageId: String): Result<Cat> {
        return try {
            Log.d(TAG, "Fetching cat image with ID: $imageId")
            val catImage = apiService.getCatImageById(imageId)
            Log.d(TAG, "Successfully fetched cat image: ${catImage.id}")
            Result.success(catImage.toDomain())
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while fetching cat image: ${e.code()} - ${e.message()}")
            Result.failure(NetworkException.HttpError(e.code(), e.message()))
        } catch (e: IOException) {
            Log.e(TAG, "Network error while fetching cat image", e)
            Result.failure(NetworkException.NetworkError(e.message ?: "Network error"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while fetching cat image", e)
            Result.failure(NetworkException.UnknownError(e.message ?: "Unknown error"))
        }
    }
    
    override suspend fun uploadCatImage(
        file: MultipartBody.Part,
        subId: String?,
        breedIds: String?
    ): Result<Cat> {
        return try {
            Log.d(TAG, "Uploading cat image")
            val response = apiService.uploadCatImage(file, subId, breedIds)
            if (response.isSuccessful && response.body() != null) {
                val uploadedImage = response.body()!!
                Log.d(TAG, "Successfully uploaded cat image: ${uploadedImage.id}")
                Result.success(uploadedImage.toDomain())
            } else {
                Log.e(TAG, "Upload failed with code: ${response.code()}")
                Result.failure(NetworkException.HttpError(response.code(), response.message()))
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while uploading image: ${e.code()} - ${e.message()}")
            Result.failure(NetworkException.HttpError(e.code(), e.message()))
        } catch (e: IOException) {
            Log.e(TAG, "Network error while uploading image", e)
            Result.failure(NetworkException.NetworkError(e.message ?: "Network error"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while uploading image", e)
            Result.failure(NetworkException.UnknownError(e.message ?: "Unknown error"))
        }
    }
    
    override suspend fun deleteCatImage(imageId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting cat image: $imageId")
            val response = apiService.deleteCatImage(imageId)
            if (response.isSuccessful) {
                Log.d(TAG, "Successfully deleted cat image: $imageId")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Delete failed with code: ${response.code()}")
                Result.failure(NetworkException.HttpError(response.code(), response.message()))
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while deleting image: ${e.code()} - ${e.message()}")
            Result.failure(NetworkException.HttpError(e.code(), e.message()))
        } catch (e: IOException) {
            Log.e(TAG, "Network error while deleting image", e)
            Result.failure(NetworkException.NetworkError(e.message ?: "Network error"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while deleting image", e)
            Result.failure(NetworkException.UnknownError(e.message ?: "Unknown error"))
        }
    }
    
    override suspend fun getBreeds(
        attachBreed: Int?,
        page: Int?,
        limit: Int?
    ): Result<List<CatBreed>> {
        return try {
            Log.d(TAG, "Fetching cat breeds")
            val breeds = apiService.getBreeds(
                attachBreed = attachBreed,
                page = page,
                limit = limit
            )
            Log.d(TAG, "Successfully fetched ${breeds.size} breeds")
            Result.success(breeds.toDomainBreeds())
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while fetching breeds: ${e.code()} - ${e.message()}")
            Result.failure(NetworkException.HttpError(e.code(), e.message()))
        } catch (e: IOException) {
            Log.e(TAG, "Network error while fetching breeds", e)
            Result.failure(NetworkException.NetworkError(e.message ?: "Network error"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while fetching breeds", e)
            Result.failure(NetworkException.UnknownError(e.message ?: "Unknown error"))
        }
    }
    
    override suspend fun searchBreeds(query: String): Result<List<CatBreed>> {
        return try {
            Log.d(TAG, "Searching breeds with query: $query")
            val breeds = apiService.searchBreeds(query)
            Log.d(TAG, "Found ${breeds.size} breeds for query: $query")
            Result.success(breeds.toDomainBreeds())
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while searching breeds: ${e.code()} - ${e.message()}")
            Result.failure(NetworkException.HttpError(e.code(), e.message()))
        } catch (e: IOException) {
            Log.e(TAG, "Network error while searching breeds", e)
            Result.failure(NetworkException.NetworkError(e.message ?: "Network error"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while searching breeds", e)
            Result.failure(NetworkException.UnknownError(e.message ?: "Unknown error"))
        }
    }
    
    override suspend fun getImagesByBreed(
        breedId: String,
        limit: Int?,
        page: Int?,
        size: String?,
        mimeTypes: String?,
        format: String?,
        order: String?,
        includeBreeds: Boolean?,
        includeCategories: Boolean?
    ): Result<List<Cat>> {
        return try {
            Log.d(TAG, "Fetching images for breed: $breedId")
            val images = apiService.getImagesByBreed(
                breedId = breedId,
                limit = limit,
                page = page,
                size = size,
                mimeTypes = mimeTypes,
                format = format,
                order = order,
                includeBreeds = includeBreeds,
                includeCategories = includeCategories
            )
            Log.d(TAG, "Successfully fetched ${images.size} images for breed: $breedId")
            Result.success(images.toDomain())
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while fetching breed images: ${e.code()} - ${e.message()}")
            Result.failure(NetworkException.HttpError(e.code(), e.message()))
        } catch (e: IOException) {
            Log.e(TAG, "Network error while fetching breed images", e)
            Result.failure(NetworkException.NetworkError(e.message ?: "Network error"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while fetching breed images", e)
            Result.failure(NetworkException.UnknownError(e.message ?: "Unknown error"))
        }
    }
    
    override suspend fun getCategories(
        limit: Int?,
        page: Int?
    ): Result<List<CatCategory>> {
        return try {
            Log.d(TAG, "Fetching categories")
            val categories = apiService.getCategories(limit = limit, page = page)
            Log.d(TAG, "Successfully fetched ${categories.size} categories")
            Result.success(categories.toDomainCategories())
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while fetching categories: ${e.code()} - ${e.message()}")
            Result.failure(NetworkException.HttpError(e.code(), e.message()))
        } catch (e: IOException) {
            Log.e(TAG, "Network error while fetching categories", e)
            Result.failure(NetworkException.NetworkError(e.message ?: "Network error"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while fetching categories", e)
            Result.failure(NetworkException.UnknownError(e.message ?: "Unknown error"))
        }
    }
    
    override suspend fun getImagesByCategory(
        categoryId: Int,
        limit: Int?,
        page: Int?,
        size: String?,
        mimeTypes: String?,
        format: String?,
        order: String?,
        includeBreeds: Boolean?,
        includeCategories: Boolean?
    ): Result<List<Cat>> {
        return try {
            Log.d(TAG, "Fetching images for category: $categoryId")
            val images = apiService.getImagesByCategory(
                categoryIds = categoryId.toString(),
                limit = limit,
                page = page,
                size = size,
                mimeTypes = mimeTypes,
                format = format,
                order = order,
                includeBreeds = includeBreeds,
                includeCategories = includeCategories
            )
            Log.d(TAG, "Successfully fetched ${images.size} images for category: $categoryId")
            Result.success(images.toDomain())
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while fetching category images: ${e.code()} - ${e.message()}")
            Result.failure(NetworkException.HttpError(e.code(), e.message()))
        } catch (e: IOException) {
            Log.e(TAG, "Network error while fetching category images", e)
            Result.failure(NetworkException.NetworkError(e.message ?: "Network error"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while fetching category images", e)
            Result.failure(NetworkException.UnknownError(e.message ?: "Unknown error"))
        }
    }
    
    override fun getRandomCatsFlow(
        limit: Int,
        size: String?,
        categoryIds: String?,
        breedIds: String?
    ): Flow<Result<List<Cat>>> = flow {
        emit(getRandomCats(
            limit = limit,
            size = size,
            categoryIds = categoryIds,
            breedIds = breedIds
        ))
    }
}

/**
 * Custom exceptions for network operations
 */
sealed class NetworkException(message: String) : Exception(message) {
    data class HttpError(val code: Int, val errorMessage: String?) : NetworkException("HTTP $code: $errorMessage")
    data class NetworkError(val errorMessage: String) : NetworkException("Network error: $errorMessage")
    data class UnknownError(val errorMessage: String) : NetworkException("Unknown error: $errorMessage")
} 