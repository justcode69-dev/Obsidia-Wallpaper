package com.example.aurawallpaper.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WallpaperDao {
    @Query("SELECT * FROM favorites ORDER BY id DESC")
    fun getAllFavorites(): Flow<List<FavoriteWallpaper>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(wallpaper: FavoriteWallpaper)

    @Query("DELETE FROM favorites WHERE id = :id")
    fun deleteFavoriteById(id: Long)

    // Collections
    @Query("SELECT * FROM collections ORDER BY id DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCollection(collection: CollectionEntity): Long

    @Query("DELETE FROM collections WHERE id = :id")
    fun deleteCollection(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWallpaperToCollection(collectionWallpaper: CollectionWallpaper)

    @Query("DELETE FROM collection_wallpapers WHERE collectionId = :collectionId AND wallpaperId = :wallpaperId")
    fun removeWallpaperFromCollection(collectionId: Long, wallpaperId: Long)

    @Query("SELECT * FROM collection_wallpapers WHERE collectionId = :collectionId")
    fun getWallpapersForCollection(collectionId: Long): Flow<List<CollectionWallpaper>>
}
