package com.example.network.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatImageModel(
    @SerialName("id")
    val id: String,
    
    @SerialName("url")
    val url: String,
    
    @SerialName("width")
    val width: Int,
    
    @SerialName("height")
    val height: Int,
    
    @SerialName("breeds")
    val breeds: List<Breed> = emptyList(),
    
    @SerialName("categories")
    val categories: List<Category> = emptyList(),
    
    @SerialName("sub_id")
    val subId: String? = null,
    
    @SerialName("created_at")
    val createdAt: String? = null,
    
    @SerialName("original_filename")
    val originalFilename: String? = null,
    
    @SerialName("breed_ids")
    val breedIds: String? = null,
    
    @SerialName("pending")
    val pending: Int? = null,
    
    @SerialName("approved")
    val approved: Int? = null
)

@Serializable
data class Breed(
    @SerialName("id")
    val id: String,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("temperament")
    val temperament: String? = null,
    
    @SerialName("origin")
    val origin: String? = null,
    
    @SerialName("country_codes")
    val countryCodes: String? = null,
    
    @SerialName("country_code")
    val countryCode: String? = null,
    
    @SerialName("description")
    val description: String? = null,
    
    @SerialName("life_span")
    val lifeSpan: String? = null,
    
    @SerialName("indoor")
    val indoor: Int? = null,
    
    @SerialName("lap")
    val lap: Int? = null,
    
    @SerialName("alt_names")
    val altNames: String? = null,
    
    @SerialName("adaptability")
    val adaptability: Int? = null,
    
    @SerialName("affection_level")
    val affectionLevel: Int? = null,
    
    @SerialName("child_friendly")
    val childFriendly: Int? = null,
    
    @SerialName("dog_friendly")
    val dogFriendly: Int? = null,
    
    @SerialName("energy_level")
    val energyLevel: Int? = null,
    
    @SerialName("grooming")
    val grooming: Int? = null,
    
    @SerialName("health_issues")
    val healthIssues: Int? = null,
    
    @SerialName("intelligence")
    val intelligence: Int? = null,
    
    @SerialName("shedding_level")
    val sheddingLevel: Int? = null,
    
    @SerialName("social_needs")
    val socialNeeds: Int? = null,
    
    @SerialName("stranger_friendly")
    val strangerFriendly: Int? = null,
    
    @SerialName("vocalisation")
    val vocalisation: Int? = null,
    
    @SerialName("experimental")
    val experimental: Int? = null,
    
    @SerialName("hairless")
    val hairless: Int? = null,
    
    @SerialName("natural")
    val natural: Int? = null,
    
    @SerialName("rare")
    val rare: Int? = null,
    
    @SerialName("rex")
    val rex: Int? = null,
    
    @SerialName("suppressed_tail")
    val suppressedTail: Int? = null,
    
    @SerialName("short_legs")
    val shortLegs: Int? = null,
    
    @SerialName("wikipedia_url")
    val wikipediaUrl: String? = null,
    
    @SerialName("hypoallergenic")
    val hypoallergenic: Int? = null,
    
    @SerialName("reference_image_id")
    val referenceImageId: String? = null,
    
    @SerialName("weight")
    val weight: Weight? = null
)

@Serializable
data class Weight(
    @SerialName("imperial")
    val imperial: String,
    
    @SerialName("metric")
    val metric: String
)

@Serializable
data class Category(
    @SerialName("id")
    val id: Int,
    
    @SerialName("name")
    val name: String
)