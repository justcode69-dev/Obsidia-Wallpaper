package com.example.aurawallpaper.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerUrl: String,
    val photographerId: Long,
    val avgColor: String? = null,
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
