package com.example.aurawallpaper.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.aurawallpaper.data.model.Photo
import com.example.aurawallpaper.ui.home.FeedMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDiscoveryScreen(
    viewModel: HomeViewModel,
    onWallpaperClick: (Photo) -> Unit
) {
    val wallpapers by viewModel.wallpapers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val errorMessage by viewModel.errorMessage.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val feedMode by viewModel.feedMode.collectAsState()

    var textSearch by remember { mutableStateOf(searchQuery) }

    LaunchedEffect(searchQuery) {
        if (searchQuery != textSearch) {
            textSearch = searchQuery
        }
    }

    val categories = listOf("Nature", "Abstract", "Minimal", "Dark", "4K", "Anime", "Architecture", "Cars")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aura Gallery", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search Bar
            OutlinedTextField(
                value = textSearch,
                onValueChange = { textSearch = it },
                placeholder = { Text("Search wallpapers...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.search(textSearch)
                }),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            )

            // Category Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category) }
                    )
                }
            }

            // Tabs for Popular / Newest
            TabRow(
                selectedTabIndex = if (feedMode == FeedMode.POPULAR) 0 else 1,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Tab(
                    selected = feedMode == FeedMode.POPULAR,
                    onClick = { viewModel.setFeedMode(FeedMode.POPULAR) },
                    text = { Text("Popular") }
                )
                Tab(
                    selected = feedMode == FeedMode.NEWEST,
                    onClick = { viewModel.setFeedMode(FeedMode.NEWEST) },
                    text = { Text("Newest") }
                )
            }

            // Grid Content
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (wallpapers.isEmpty() && isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (errorMessage != null && wallpapers.isEmpty()) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(wallpapers.size) { index ->
                            if (index == wallpapers.lastIndex) {
                                LaunchedEffect(index) {
                                    viewModel.loadNextPage()
                                }
                            }
                            WallpaperCard(
                                photo = wallpapers[index],
                                onClick = { onWallpaperClick(wallpapers[index]) }
                            )
                        }
                    }

                    // Loading indicator for pagination
                    if (isLoading && wallpapers.isNotEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WallpaperCard(photo: Photo, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // approximate height based on photo ratio
            .height((200 + (photo.height.toFloat() / photo.width.toFloat() * 100)).dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = photo.src.medium,
            contentDescription = photo.alt,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Subtle gradient overlay for metadata can be added here
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(8.dp)
        ) {
            Text(
                text = photo.photographer,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
