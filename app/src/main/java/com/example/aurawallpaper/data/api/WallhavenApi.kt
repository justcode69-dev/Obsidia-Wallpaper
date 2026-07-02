package com.example.aurawallpaper.data.api

import com.example.aurawallpaper.data.model.WallhavenResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WallhavenApi {
    @GET("v1/search")
    suspend fun searchWallpapers(
        @Query("q") query: String? = null,
        @Query("categories") categories: String = "111", // General, Anime, People (1=on, 0=off)
        @Query("purity") purity: String = "100", // SFW only
        @Query("sorting") sorting: String = "toplist",
        @Query("ratios") ratios: String = "9x16,10x16,9x18,9x21", // Portrait mobile ratios
        @Query("page") page: Int = 1
    ): WallhavenResponse
}
