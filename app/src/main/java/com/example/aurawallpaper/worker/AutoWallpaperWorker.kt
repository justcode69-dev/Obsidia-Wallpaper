package com.example.aurawallpaper.worker

import android.app.WallpaperManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.aurawallpaper.Graph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AutoWallpaperWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val repository = Graph.wallpaperRepository
                // We use first() to get a single snapshot of the flow
                val favorites = repository.dao.getAllFavorites().first()
                if (favorites.isEmpty()) {
                    return@withContext Result.success()
                }

                // Pick a random favorite
                val randomWallpaper = favorites.random()

                // Download bitmap
                val loader = ImageLoader(applicationContext)
                val request = ImageRequest.Builder(applicationContext)
                    .data(randomWallpaper.originalUrl)
                    .allowHardware(false)
                    .build()

                val result = loader.execute(request)
                if (result is SuccessResult) {
                    val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
                    if (bitmap != null) {
                        // Set wallpaper
                        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                        wallpaperManager.setBitmap(bitmap)
                    }
                }

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }
}
