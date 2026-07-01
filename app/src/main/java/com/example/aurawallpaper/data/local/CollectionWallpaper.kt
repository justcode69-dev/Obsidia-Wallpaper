package com.example.aurawallpaper.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "collection_wallpapers",
    primaryKeys = ["collectionId", "wallpaperId"],
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("collectionId")]
)
data class CollectionWallpaper(
    val collectionId: Long,
    val wallpaperId: Long,
    val url: String,
    val alt: String,
    val photographer: String
)
