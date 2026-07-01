package com.example.aurawallpaper.ui.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.aurawallpaper.data.model.Photo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onWallpaperClick: (Photo) -> Unit,
    viewModel: FavoritesViewModel = viewModel()
) {
    val favorites by viewModel.favorites.collectAsState()

    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("AuraPrefs", Context.MODE_PRIVATE)
    var isAutoChangerEnabled by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(sharedPrefs.getBoolean("auto_changer", false)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Auto-Rotate",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Switch(
                            checked = isAutoChangerEnabled,
                            onCheckedChange = { isChecked ->
                                isAutoChangerEnabled = isChecked
                                sharedPrefs.edit().putBoolean("auto_changer", isChecked).apply()
                                
                                val workManager = androidx.work.WorkManager.getInstance(context)
                                if (isChecked) {
                                    val constraints = androidx.work.Constraints.Builder()
                                        .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                                        .build()
                                    val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.aurawallpaper.worker.AutoWallpaperWorker>(24, java.util.concurrent.TimeUnit.HOURS)
                                        .setConstraints(constraints)
                                        .build()
                                    workManager.enqueueUniquePeriodicWork("AutoWallpaperChanger", androidx.work.ExistingPeriodicWorkPolicy.UPDATE, workRequest)
                                    android.widget.Toast.makeText(context, "Auto-Wallpaper enabled (rotates daily)", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    workManager.cancelUniqueWork("AutoWallpaperChanger")
                                    android.widget.Toast.makeText(context, "Auto-Wallpaper disabled", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorites yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                items(favorites) { photo ->
                    AsyncImage(
                        model = photo.src.large,
                        contentDescription = photo.alt,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((200..300).random().dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onWallpaperClick(photo) }
                    )
                }
            }
        }
    }
}
