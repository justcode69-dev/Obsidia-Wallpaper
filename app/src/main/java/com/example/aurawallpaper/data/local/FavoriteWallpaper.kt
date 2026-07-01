package com.example.aurawallpaper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteWallpaper(
    @PrimaryKey val id: Long,
    val url: String,
    val photographer: String,
    val originalUrl: String,
    val photographerUrl: String,
    val altText: String
)
