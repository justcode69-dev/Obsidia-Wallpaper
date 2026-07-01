package com.example.aurawallpaper.data.api

import com.example.aurawallpaper.data.model.PexelsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PexelsApi {
    @GET("v1/curated")
    suspend fun getCuratedWallpapers(
        @Header("Authorization") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): PexelsResponse

    @GET("v1/search")
    suspend fun searchWallpapers(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): PexelsResponse
}
