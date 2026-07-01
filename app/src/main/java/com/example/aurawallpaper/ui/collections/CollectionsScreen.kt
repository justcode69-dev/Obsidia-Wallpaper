package com.example.aurawallpaper.ui.collections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.aurawallpaper.Graph
import com.example.aurawallpaper.data.local.CollectionEntity
import com.example.aurawallpaper.data.local.CollectionWallpaper
import com.example.aurawallpaper.data.model.Photo
import com.example.aurawallpaper.data.model.PhotoSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(onWallpaperClick: (Photo) -> Unit) {
    val repository = Graph.wallpaperRepository
    var collections by remember { mutableStateOf<List<CollectionEntity>>(emptyList()) }
    var selectedCollection by remember { mutableStateOf<CollectionEntity?>(null) }
    var collectionWallpapers by remember { mutableStateOf<List<CollectionWallpaper>>(emptyList()) }

    LaunchedEffect(Unit) {
        repository.dao.getAllCollections().collect { collections = it }
    }

    LaunchedEffect(selectedCollection) {
        selectedCollection?.let { collection ->
            repository.dao.getWallpapersForCollection(collection.id).collect { collectionWallpapers = it }
        }
    }

    if (selectedCollection == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(title = { Text("My Collections") })
            if (collections.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No custom collections yet.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(collections) { collection ->
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .height(160.dp)
                                .clickable { selectedCollection = collection }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (collection.coverUrl != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(collection.coverUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                                ) {
                                    Text(
                                        text = collection.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = { Text(selectedCollection!!.name) },
                navigationIcon = {
                    IconButton(onClick = { selectedCollection = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
            if (collectionWallpapers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No wallpapers in this collection.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(collectionWallpapers) { cw ->
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .aspectRatio(0.6f)
                                .clickable {
                                    onWallpaperClick(
                                        Photo(
                                            id = cw.wallpaperId,
                                            width = 1080,
                                            height = 1920,
                                            url = cw.url,
                                            photographer = cw.photographer,
                                            photographerUrl = "",
                                            photographerId = 0,
                                            src = PhotoSource(
                                                original = cw.url,
                                                large2x = cw.url,
                                                large = cw.url,
                                                medium = cw.url,
                                                small = cw.url,
                                                portrait = cw.url,
                                                landscape = cw.url,
                                                tiny = cw.url
                                            ),
                                            liked = false,
                                            alt = cw.alt
                                        )
                                    )
                                }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(cw.url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = cw.alt,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
