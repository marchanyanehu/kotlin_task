package com.example.network.domain.model

/**
 * Domain model for Cat entity
 * This model is independent of data layer and represents business logic
 */
data class Cat(
    val id: String,
    val imageUrl: String,
    val width: Int,
    val height: Int,
    val breeds: List<CatBreed> = emptyList(),
    val categories: List<CatCategory> = emptyList(),
    val subId: String? = null,
    val createdAt: String? = null,
    val originalFilename: String? = null,
    val breedIds: String? = null,
    val pending: Int? = null,
    val approved: Int? = null
)

/**
 * Domain model for Cat Breed
 */
data class CatBreed(
    val id: String,
    val name: String,
    val temperament: String? = null,
    val origin: String? = null,
    val countryCodes: String? = null,
    val countryCode: String? = null,
    val description: String? = null,
    val lifeSpan: String? = null,
    val indoor: Int? = null,
    val lap: Int? = null,
    val altNames: String? = null,
    val adaptability: Int? = null,
    val affectionLevel: Int? = null,
    val childFriendly: Int? = null,
    val dogFriendly: Int? = null,
    val energyLevel: Int? = null,
    val grooming: Int? = null,
    val healthIssues: Int? = null,
    val intelligence: Int? = null,
    val sheddingLevel: Int? = null,
    val socialNeeds: Int? = null,
    val strangerFriendly: Int? = null,
    val vocalisation: Int? = null,
    val experimental: Int? = null,
    val hairless: Int? = null,
    val natural: Int? = null,
    val rare: Int? = null,
    val rex: Int? = null,
    val suppressedTail: Int? = null,
    val shortLegs: Int? = null,
    val wikipediaUrl: String? = null,
    val hypoallergenic: Int? = null,
    val referenceImageId: String? = null,
    val weight: CatWeight? = null
)

/**
 * Domain model for Cat Weight
 */
data class CatWeight(
    val imperial: String,
    val metric: String
)

/**
 * Domain model for Cat Category
 */
data class CatCategory(
    val id: Int,
    val name: String
) 