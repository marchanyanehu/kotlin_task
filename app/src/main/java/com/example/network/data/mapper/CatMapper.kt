package com.example.network.data.mapper

import com.example.network.data.model.Breed as DataBreed
import com.example.network.data.model.Category as DataCategory
import com.example.network.data.model.CatImageModel
import com.example.network.data.model.Weight as DataWeight
import com.example.network.domain.model.Cat
import com.example.network.domain.model.CatBreed
import com.example.network.domain.model.CatCategory
import com.example.network.domain.model.CatWeight

/**
 * Mapper for converting between data and domain models
 * This ensures separation of concerns and independence between layers
 */
object CatMapper {
    
    /**
     * Convert data model to domain model
     */
    fun CatImageModel.toDomain(): Cat {
        return Cat(
            id = this.id,
            imageUrl = this.url,
            width = this.width,
            height = this.height,
            breeds = this.breeds.map { it.toDomain() },
            categories = this.categories.map { it.toDomain() },
            subId = this.subId,
            createdAt = this.createdAt,
            originalFilename = this.originalFilename,
            breedIds = this.breedIds,
            pending = this.pending,
            approved = this.approved
        )
    }
    
    /**
     * Convert list of data models to domain models
     */
    fun List<CatImageModel>.toDomain(): List<Cat> {
        return this.map { it.toDomain() }
    }
    
    /**
     * Convert data breed to domain breed
     */
    fun DataBreed.toDomain(): CatBreed {
        return CatBreed(
            id = this.id,
            name = this.name,
            temperament = this.temperament,
            origin = this.origin,
            countryCodes = this.countryCodes,
            countryCode = this.countryCode,
            description = this.description,
            lifeSpan = this.lifeSpan,
            indoor = this.indoor,
            lap = this.lap,
            altNames = this.altNames,
            adaptability = this.adaptability,
            affectionLevel = this.affectionLevel,
            childFriendly = this.childFriendly,
            dogFriendly = this.dogFriendly,
            energyLevel = this.energyLevel,
            grooming = this.grooming,
            healthIssues = this.healthIssues,
            intelligence = this.intelligence,
            sheddingLevel = this.sheddingLevel,
            socialNeeds = this.socialNeeds,
            strangerFriendly = this.strangerFriendly,
            vocalisation = this.vocalisation,
            experimental = this.experimental,
            hairless = this.hairless,
            natural = this.natural,
            rare = this.rare,
            rex = this.rex,
            suppressedTail = this.suppressedTail,
            shortLegs = this.shortLegs,
            wikipediaUrl = this.wikipediaUrl,
            hypoallergenic = this.hypoallergenic,
            referenceImageId = this.referenceImageId,
            weight = this.weight?.toDomain()
        )
    }
    
    /**
     * Convert list of data breeds to domain breeds
     */
    fun List<DataBreed>.toDomainBreeds(): List<CatBreed> {
        return this.map { it.toDomain() }
    }
    
    /**
     * Convert data weight to domain weight
     */
    fun DataWeight.toDomain(): CatWeight {
        return CatWeight(
            imperial = this.imperial,
            metric = this.metric
        )
    }
    
    /**
     * Convert data category to domain category
     */
    fun DataCategory.toDomain(): CatCategory {
        return CatCategory(
            id = this.id,
            name = this.name
        )
    }
    
    /**
     * Convert list of data categories to domain categories
     */
    fun List<DataCategory>.toDomainCategories(): List<CatCategory> {
        return this.map { it.toDomain() }
    }
    
    // Reverse mappings (domain to data) - if needed for uploads/updates
    
    /**
     * Convert domain model to data model
     */
    fun Cat.toData(): CatImageModel {
        return CatImageModel(
            id = this.id,
            url = this.imageUrl,
            width = this.width,
            height = this.height,
            breeds = this.breeds.map { it.toData() },
            categories = this.categories.map { it.toData() },
            subId = this.subId,
            createdAt = this.createdAt,
            originalFilename = this.originalFilename,
            breedIds = this.breedIds,
            pending = this.pending,
            approved = this.approved
        )
    }
    
    /**
     * Convert domain breed to data breed
     */
    fun CatBreed.toData(): DataBreed {
        return DataBreed(
            id = this.id,
            name = this.name,
            temperament = this.temperament,
            origin = this.origin,
            countryCodes = this.countryCodes,
            countryCode = this.countryCode,
            description = this.description,
            lifeSpan = this.lifeSpan,
            indoor = this.indoor,
            lap = this.lap,
            altNames = this.altNames,
            adaptability = this.adaptability,
            affectionLevel = this.affectionLevel,
            childFriendly = this.childFriendly,
            dogFriendly = this.dogFriendly,
            energyLevel = this.energyLevel,
            grooming = this.grooming,
            healthIssues = this.healthIssues,
            intelligence = this.intelligence,
            sheddingLevel = this.sheddingLevel,
            socialNeeds = this.socialNeeds,
            strangerFriendly = this.strangerFriendly,
            vocalisation = this.vocalisation,
            experimental = this.experimental,
            hairless = this.hairless,
            natural = this.natural,
            rare = this.rare,
            rex = this.rex,
            suppressedTail = this.suppressedTail,
            shortLegs = this.shortLegs,
            wikipediaUrl = this.wikipediaUrl,
            hypoallergenic = this.hypoallergenic,
            referenceImageId = this.referenceImageId,
            weight = this.weight?.toData()
        )
    }
    
    /**
     * Convert domain weight to data weight
     */
    fun CatWeight.toData(): DataWeight {
        return DataWeight(
            imperial = this.imperial,
            metric = this.metric
        )
    }
    
    /**
     * Convert domain category to data category
     */
    fun CatCategory.toData(): DataCategory {
        return DataCategory(
            id = this.id,
            name = this.name
        )
    }
} 