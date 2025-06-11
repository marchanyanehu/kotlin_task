package com.example.network.domain.di

import com.example.network.data.repository.CatRepositoryImpl
import com.example.network.domain.repository.CatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for domain layer dependencies
 * This module provides domain layer abstractions and their implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {
    
    /**
     * Binds CatRepositoryImpl to CatRepository interface
     * This enables dependency inversion - domain layer depends on abstraction,
     * not concrete implementation
     */
    @Binds
    @Singleton
    abstract fun bindCatRepository(
        catRepositoryImpl: CatRepositoryImpl
    ): CatRepository
} 