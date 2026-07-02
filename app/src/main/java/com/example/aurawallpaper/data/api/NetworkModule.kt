package com.example.aurawallpaper.data.api

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object NetworkModule {
    private const val WALLHAVEN_BASE_URL = "https://wallhaven.cc/api/"
    private const val REDDIT_BASE_URL = "https://www.reddit.com/"

    private val json = Json { ignoreUnknownKeys = true }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val wallhavenRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WALLHAVEN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private val redditRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(REDDIT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val wallhavenApi: WallhavenApi by lazy {
        wallhavenRetrofit.create(WallhavenApi::class.java)
    }

    val redditApi: RedditApi by lazy {
        redditRetrofit.create(RedditApi::class.java)
    }
}
