package com.example.aurawallpaper

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.aurawallpaper.ui.home.HomeDiscoveryScreen
import com.example.aurawallpaper.ui.home.HomeViewModel
import com.example.aurawallpaper.ui.preview.WallpaperPreviewScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import com.example.aurawallpaper.data.model.Photo
import kotlinx.serialization.Serializable

import androidx.navigation3.runtime.NavKey

@Serializable
data class CategoryFeed(val categoryName: String) : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Main)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = Modifier.fillMaxSize(),
        entryProvider = entryProvider {
            entry<Main> {
                MainHost(
                    onWallpaperClick = { photo ->
                        backStack.add(Preview(photo))
                    },
                    onCategoryClick = { categoryName ->
                        backStack.add(CategoryFeed(categoryName))
                    }
                )
            }
            entry<Preview> { navKey ->
                WallpaperPreviewScreen(
                    photo = navKey.photo,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<CategoryFeed> { navKey ->
                val viewModel: HomeViewModel = viewModel()
                androidx.compose.runtime.LaunchedEffect(navKey.categoryName) {
                    viewModel.search(navKey.categoryName)
                }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(navKey.categoryName) },
                            navigationIcon = {
                                IconButton(onClick = { backStack.removeLastOrNull() }) {
                                    Icon(Icons.Default.ArrowBack, "Back")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        HomeDiscoveryScreen(
                            viewModel = viewModel,
                            onWallpaperClick = { photo ->
                                backStack.add(Preview(photo))
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun MainHost(onWallpaperClick: (Photo) -> Unit, onCategoryClick: (String) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
                    label = { Text("Categories") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Collections, contentDescription = "Collections") },
                    label = { Text("Collections") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (selectedTab) {
                0 -> {
                    val homeViewModel: HomeViewModel = viewModel()
                    HomeDiscoveryScreen(
                        viewModel = homeViewModel,
                        onWallpaperClick = onWallpaperClick
                    )
                }
                1 -> {
                    com.example.aurawallpaper.ui.categories.CategoriesScreen(
                        onCategoryClick = onCategoryClick
                    )
                }
                2 -> {
                    val favoritesViewModel: com.example.aurawallpaper.ui.favorites.FavoritesViewModel = viewModel()
                    com.example.aurawallpaper.ui.favorites.FavoritesScreen(
                        viewModel = favoritesViewModel,
                        onWallpaperClick = onWallpaperClick
                    )
                }
                3 -> {
                    com.example.aurawallpaper.ui.collections.CollectionsScreen(
                        onWallpaperClick = onWallpaperClick
                    )
                }
            }
        }
    }
}
