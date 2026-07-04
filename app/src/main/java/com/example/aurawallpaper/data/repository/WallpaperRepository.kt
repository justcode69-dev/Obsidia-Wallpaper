package com.example.aurawallpaper.data.repository

import com.example.aurawallpaper.data.api.NetworkModule
import com.example.aurawallpaper.data.model.Photo
import com.example.aurawallpaper.data.model.PhotoSource
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull

class WallpaperRepository(val dao: com.example.aurawallpaper.data.local.WallpaperDao) {
    private val wallhavenApi = NetworkModule.wallhavenApi
    private val redditApi = NetworkModule.redditApi

    suspend fun getCuratedWallpapers(page: Int = 1): Result<List<Photo>> {
        return try {
            supervisorScope {
                val wallhavenDeferred = async { wallhavenApi.searchWallpapers(sorting = "toplist", page = page) }
                // Reddit pagination by page number is not directly supported without an 'after' token,
                // but we can fetch new batches of hot occasionally or just randomize.
                // For simplicity, we just fetch a popular wallpaper subreddit.
                val redditDeferred = async { redditApi.getSubredditHot("Amoledbackgrounds", limit = 50) }

                val photos = mutableListOf<Photo>()
                
                try {
                    val wallhavenResponse = wallhavenDeferred.await()
                    photos.addAll(wallhavenResponse.data.map { w ->
                        Photo(
                            id = w.id.hashCode().toLong(),
                            width = w.dimensionX,
                            height = w.dimensionY,
                            url = w.shortUrl,
                            photographer = w.source.ifEmpty { "Wallhaven" },
                            photographerUrl = w.shortUrl,
                            photographerId = 0,
                            avgColor = w.colors.firstOrNull(),
                            src = PhotoSource(
                                original = w.path,
                                large2x = w.path,
                                large = w.path,
                                medium = w.thumbs.large,
                                small = w.thumbs.small,
                                portrait = w.path,
                                landscape = w.path,
                                tiny = w.thumbs.small
                            ),
                            liked = false,
                            alt = w.category
                        )
                    })
                } catch (e: Exception) { e.printStackTrace() }

                try {
                    val redditResponse = withTimeoutOrNull(2500) { redditDeferred.await() }
                    if (redditResponse != null) {
                        photos.addAll(redditResponse.data.children.mapNotNull { child ->
                            val post = child.data
                            val img = post.preview?.images?.firstOrNull()?.source ?: return@mapNotNull null
                            Photo(
                                id = post.id.hashCode().toLong(),
                                width = img.width,
                                height = img.height,
                                url = post.url,
                                photographer = post.author,
                                photographerUrl = "https://reddit.com/user/${post.author}",
                                photographerId = 0,
                                avgColor = null,
                                src = PhotoSource(
                                    original = post.url,
                                    large2x = post.url,
                                    large = post.url,
                                    medium = img.url.replace("&amp;", "&"),
                                    small = img.url.replace("&amp;", "&"),
                                    portrait = post.url,
                                    landscape = post.url,
                                    tiny = img.url.replace("&amp;", "&")
                                ),
                                liked = false,
                                alt = post.title
                            )
                        })
                    }
                } catch (e: Exception) { e.printStackTrace() }

                Result.success(photos.shuffled())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun searchWallpapers(query: String, page: Int = 1): Result<List<Photo>> {
        return try {
            supervisorScope {
                val wallhavenDeferred = async { wallhavenApi.searchWallpapers(query = query, page = page) }

                val photos = mutableListOf<Photo>()
                
                try {
                    val wallhavenResponse = wallhavenDeferred.await()
                    photos.addAll(wallhavenResponse.data.map { w ->
                        Photo(
                            id = w.id.hashCode().toLong(),
                            width = w.dimensionX,
                            height = w.dimensionY,
                            url = w.shortUrl,
                            photographer = w.source.ifEmpty { "Wallhaven" },
                            photographerUrl = w.shortUrl,
                            photographerId = 0,
                            avgColor = w.colors.firstOrNull(),
                            src = PhotoSource(
                                original = w.path,
                                large2x = w.path,
                                large = w.path,
                                medium = w.thumbs.large,
                                small = w.thumbs.small,
                                portrait = w.path,
                                landscape = w.path,
                                tiny = w.thumbs.small
                            ),
                            liked = false,
                            alt = w.category
                        )
                    })
                } catch (e: Exception) { e.printStackTrace() }

                Result.success(photos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
