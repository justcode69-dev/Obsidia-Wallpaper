package com.example.aurawallpaper

import androidx.navigation3.runtime.NavKey
import com.example.aurawallpaper.data.model.Photo
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey

@Serializable data class Preview(val photo: Photo) : NavKey
