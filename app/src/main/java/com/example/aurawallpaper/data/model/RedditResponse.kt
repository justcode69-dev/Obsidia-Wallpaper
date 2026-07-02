package com.example.aurawallpaper.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RedditResponse(
    val data: RedditData
)

@Serializable
data class RedditData(
    val after: String?,
    val children: List<RedditPost>
)

@Serializable
data class RedditPost(
    val data: RedditPostData
)

@Serializable
data class RedditPostData(
    val id: String,
    val title: String,
    val url: String,
    val author: String,
    val preview: RedditPreview? = null
)

@Serializable
data class RedditPreview(
    val images: List<RedditImage>
)

@Serializable
data class RedditImage(
    val source: RedditImageSource,
    val resolutions: List<RedditImageSource>
)

@Serializable
data class RedditImageSource(
    val url: String,
    val width: Int,
    val height: Int
)
