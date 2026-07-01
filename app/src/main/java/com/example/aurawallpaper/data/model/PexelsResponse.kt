package com.example.aurawallpaper.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PexelsResponse(
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    val photos: List<Photo>
)

@Serializable
data class Photo(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerialName("photographer_url") val photographerUrl: String,
    @SerialName("photographer_id") val photographerId: Long,
    @SerialName("avg_color") val avgColor: String? = null,
    val src: PhotoSource,
    val liked: Boolean,
    val alt: String
)

@Serializable
data class PhotoSource(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)
