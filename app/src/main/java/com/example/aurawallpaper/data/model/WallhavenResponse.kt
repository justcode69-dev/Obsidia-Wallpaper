package com.example.aurawallpaper.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class WallhavenResponse(
    val data: List<WallhavenWallpaper>,
    val meta: WallhavenMeta
)

@Serializable
data class WallhavenWallpaper(
    val id: String,
    val url: String,
    @SerialName("short_url") val shortUrl: String,
    val views: Long,
    val favorites: Long,
    val source: String,
    val purity: String,
    val category: String,
    @SerialName("dimension_x") val dimensionX: Int,
    @SerialName("dimension_y") val dimensionY: Int,
    val resolution: String,
    val ratio: String,
    @SerialName("file_size") val fileSize: Long,
    @SerialName("file_type") val fileType: String,
    @SerialName("created_at") val createdAt: String,
    val colors: List<String>,
    val path: String,
    val thumbs: WallhavenThumbs
)

@Serializable
data class WallhavenThumbs(
    val large: String,
    val original: String,
    val small: String
)

@Serializable
data class WallhavenMeta(
    @SerialName("current_page") val currentPage: Int,
    @SerialName("last_page") val lastPage: Int,
    @SerialName("per_page") val perPage: Int,
    val total: Long
)
