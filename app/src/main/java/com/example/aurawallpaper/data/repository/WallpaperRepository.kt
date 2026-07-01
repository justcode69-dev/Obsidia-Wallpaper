package com.example.aurawallpaper.data.repository

import com.example.aurawallpaper.data.api.NetworkModule
import com.example.aurawallpaper.data.model.Photo

import com.example.aurawallpaper.BuildConfig

class WallpaperRepository(val dao: com.example.aurawallpaper.data.local.WallpaperDao) {
    private val api = NetworkModule.pexelsApi
    private val apiKey = BuildConfig.PEXELS_API_KEY

    suspend fun getCuratedWallpapers(page: Int = 1): Result<List<Photo>> {
        return try {
            val response = api.getCuratedWallpapers(apiKey, page)
            Result.success(response.photos)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun searchWallpapers(query: String, page: Int = 1): Result<List<Photo>> {
        return try {
            val response = api.searchWallpapers(apiKey, query, page)
            Result.success(response.photos)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
