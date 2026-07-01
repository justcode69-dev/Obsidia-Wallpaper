package com.example.aurawallpaper.ui.preview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aurawallpaper.data.local.CollectionEntity
import com.example.aurawallpaper.data.local.CollectionWallpaper
import com.example.aurawallpaper.data.model.Photo
import com.example.aurawallpaper.data.repository.WallpaperRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsDialog(
    photo: Photo,
    repository: WallpaperRepository,
    onDismiss: () -> Unit
) {
    var collections by remember { mutableStateOf<List<CollectionEntity>>(emptyList()) }
    var showCreateDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        repository.dao.getAllCollections().collect {
            collections = it
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save to Collection") },
        text = {
            Column {
                if (collections.isEmpty()) {
                    Text("No collections found.", modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        items(collections) { collection ->
                            ListItem(
                                headlineContent = { Text(collection.name) },
                                modifier = Modifier.clickable {
                                    coroutineScope.launch {
                                        withContext(Dispatchers.IO) {
                                            repository.dao.addWallpaperToCollection(
                                                CollectionWallpaper(
                                                    collectionId = collection.id,
                                                    wallpaperId = photo.id,
                                                    url = photo.src.original,
                                                    alt = photo.alt,
                                                    photographer = photo.photographer
                                                )
                                            )
                                        }
                                        onDismiss()
                                    }
                                }
                            )
                        }
                    }
                }
                
                TextButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Collection")
                    Spacer(Modifier.width(8.dp))
                    Text("Create New Collection")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showCreateDialog) {
        var newCollectionName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New Collection") },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    label = { Text("Collection Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCollectionName.isNotBlank()) {
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    val newId = repository.dao.insertCollection(
                                        CollectionEntity(name = newCollectionName, coverUrl = photo.src.original)
                                    )
                                    repository.dao.addWallpaperToCollection(
                                        CollectionWallpaper(
                                            collectionId = newId,
                                            wallpaperId = photo.id,
                                            url = photo.src.original,
                                            alt = photo.alt,
                                            photographer = photo.photographer
                                        )
                                    )
                                }
                                showCreateDialog = false
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Text("Create & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
