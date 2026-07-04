package com.example.aurawallpaper.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
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

    val categories = listOf("Amoled", "Anime", "Digital Art", "Cyberpunk", "Minimalist", "Space", "Abstract", "Fantasy", "Landscape", "Architecture", "Cars", "Animals", "Neon", "Gaming", "Vaporwave", "Photography")

    Scaffold(
        topBar = {
            // Remove the default app bar, Zedge doesn't have one, just a search bar at the top of the body
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search Bar
            OutlinedTextField(
                value = textSearch,
                onValueChange = { textSearch = it },
                placeholder = { Text("Search Aura...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.search(textSearch)
                }),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionIcon(icon = Icons.Default.Star, label = "Popular", selected = feedMode == FeedMode.POPULAR, onClick = { viewModel.setFeedMode(FeedMode.POPULAR) }, color = Color(0xFF9C27B0))
                QuickActionIcon(icon = Icons.Default.AutoAwesome, label = "Newest", selected = feedMode == FeedMode.NEWEST, onClick = { viewModel.setFeedMode(FeedMode.NEWEST) }, color = Color(0xFFE91E63))
                QuickActionIcon(icon = Icons.Default.Category, label = "Categories", selected = false, onClick = { viewModel.selectCategory("Random") }, color = Color(0xFF4CAF50))
                QuickActionIcon(icon = Icons.Default.WorkspacePremium, label = "Premium", selected = false, onClick = { }, color = Color(0xFFFFC107))
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
                        columns = StaggeredGridCells.Fixed(3), // 3 columns like Zedge
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Column {
                                Text(
                                    "Featured",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(wallpapers.take(4)) { photo ->
                                        FeaturedCard(photo, onClick = { onWallpaperClick(photo) })
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Popular Collections",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(wallpapers.drop(4).take(5)) { photo ->
                                        CollectionCard(photo, onClick = { onWallpaperClick(photo) })
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Popular",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                                )
                            }
                        }

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
fun QuickActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, selected: Boolean, onClick: () -> Unit, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (selected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun FeaturedCard(photo: Photo, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(260.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = photo.src.large,
            contentDescription = photo.alt,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
                .padding(12.dp)
        ) {
            Text(photo.alt.takeIf { it.isNotBlank() } ?: "Featured", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CollectionCard(photo: Photo, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = photo.src.medium,
            contentDescription = photo.alt,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))
                .padding(8.dp)
        ) {
            Text(photo.photographer, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun WallpaperCard(photo: Photo, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = photo.src.medium,
            contentDescription = photo.alt,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
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
