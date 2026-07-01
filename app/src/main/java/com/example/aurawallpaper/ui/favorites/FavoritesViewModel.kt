package com.example.aurawallpaper.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurawallpaper.Graph
import com.example.aurawallpaper.data.local.FavoriteWallpaper
import com.example.aurawallpaper.data.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: com.example.aurawallpaper.data.repository.WallpaperRepository = Graph.wallpaperRepository) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Photo>>(emptyList())
    val favorites: StateFlow<List<Photo>> = _favorites.asStateFlow()

    init {
        viewModelScope.launch {
            repository.dao.getAllFavorites().collect { favList ->
                // Convert FavoriteWallpaper back to Photo to reuse UI
                val photoList = favList.map { fav ->
                    Photo(
                        id = fav.id,
                        width = 0,
                        height = 0,
                        url = fav.url,
                        photographer = fav.photographer,
                        photographerUrl = fav.photographerUrl,
                        photographerId = 0,
                        avgColor = "",
                        src = com.example.aurawallpaper.data.model.PhotoSource(
                            original = fav.originalUrl,
                            large2x = fav.originalUrl,
                            large = fav.originalUrl,
                            medium = "",
                            small = "",
                            portrait = "",
                            landscape = "",
                            tiny = ""
                        ),
                        liked = true,
                        alt = fav.altText
                    )
                }
                _favorites.value = photoList
            }
        }
    }
}
