package com.example.aurawallpaper

import android.app.Application
import com.example.aurawallpaper.data.local.AppDatabase
import com.example.aurawallpaper.data.repository.WallpaperRepository

class AuraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}

object Graph {
    lateinit var database: AppDatabase
        private set

    val wallpaperRepository by lazy {
        WallpaperRepository(database.wallpaperDao())
    }

    fun provide(context: android.content.Context) {
        database = AppDatabase.getDatabase(context)
    }
}
