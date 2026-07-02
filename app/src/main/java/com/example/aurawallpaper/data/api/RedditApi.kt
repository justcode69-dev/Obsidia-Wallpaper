package com.example.aurawallpaper.data.api

import com.example.aurawallpaper.data.model.RedditResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditApi {
    @GET("r/{subreddit}/hot.json")
    suspend fun getSubredditHot(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null,
        @Header("User-Agent") userAgent: String = "android:com.example.aurawallpaper:v1.0 (by /u/aura)"
    ): RedditResponse
    
    @GET("r/{subreddit}/search.json")
    suspend fun searchSubreddit(
        @Path("subreddit") subreddit: String,
        @Query("q") query: String,
        @Query("restrict_sr") restrictSr: String = "on",
        @Query("sort") sort: String = "hot",
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null,
        @Header("User-Agent") userAgent: String = "android:com.example.aurawallpaper:v1.0 (by /u/aura)"
    ): RedditResponse
}
