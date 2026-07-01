package com.example.aurawallpaper.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurawallpaper.data.model.Photo
import com.example.aurawallpaper.data.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class FeedMode { POPULAR, NEWEST }

class HomeViewModel(private val repository: WallpaperRepository = com.example.aurawallpaper.Graph.wallpaperRepository) : ViewModel() {

    private val _wallpapers = MutableStateFlow<List<Photo>>(emptyList())
    val wallpapers: StateFlow<List<Photo>> = _wallpapers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentPage = 1
    private var currentQuery = ""
    private var isLastPage = false

    private val _feedMode = MutableStateFlow(FeedMode.POPULAR)
    val feedMode: StateFlow<FeedMode> = _feedMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    init {
        fetchWallpapers()
    }

    fun search(query: String) {
        _searchQuery.value = query
        _selectedCategory.value = null
        _feedMode.value = FeedMode.POPULAR // Search overrides feed mode visually or we can keep it
        currentQuery = query
        currentPage = 1
        isLastPage = false
        _wallpapers.value = emptyList()
        fetchWallpapers()
    }

    fun selectCategory(category: String) {
        if (_selectedCategory.value == category) {
            clearSearch()
            return
        }
        _selectedCategory.value = category
        _searchQuery.value = ""
        currentQuery = category
        currentPage = 1
        isLastPage = false
        _wallpapers.value = emptyList()
        fetchWallpapers()
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        currentQuery = ""
        currentPage = 1
        isLastPage = false
        _wallpapers.value = emptyList()
        fetchWallpapers()
    }

    fun loadNextPage() {
        if (_isLoading.value || isLastPage) return
        currentPage++
        fetchWallpapers()
    }

    fun setFeedMode(mode: FeedMode) {
        if (_feedMode.value == mode && _searchQuery.value.isEmpty() && _selectedCategory.value == null) return
        _feedMode.value = mode
        _searchQuery.value = ""
        _selectedCategory.value = null
        currentQuery = if (mode == FeedMode.NEWEST) "new wallpapers" else ""
        currentPage = 1
        isLastPage = false
        _wallpapers.value = emptyList()
        fetchWallpapers()
    }

    private fun fetchWallpapers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = if (currentQuery.isNotEmpty()) {
                repository.searchWallpapers(currentQuery, currentPage)
            } else {
                repository.getCuratedWallpapers(currentPage)
            }
            
            result.onSuccess { photos ->
                if (photos.isEmpty()) {
                    isLastPage = true
                } else {
                    val currentList = _wallpapers.value.toMutableList()
                    currentList.addAll(photos)
                    // Ensure no duplicates
                    _wallpapers.value = currentList.distinctBy { it.id }
                }
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "An unknown error occurred"
            }
            _isLoading.value = false
        }
    }
}
