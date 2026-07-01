package com.example.aurawallpaper.ui.preview

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.aurawallpaper.data.model.Photo
import com.example.aurawallpaper.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperPreviewScreen(
    photo: Photo,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSettingWallpaper by remember { mutableStateOf(false) }
    val (showBottomSheet, setShowBottomSheet) = remember { mutableStateOf(false) }
    var showOverlay by remember { mutableStateOf(false) }
    val (showAdjustments, setShowAdjustments) = remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var showCollectionsDialog by remember { mutableStateOf(false) }
    
    // Shader States
    var blurRadius by remember { mutableFloatStateOf(0f) }
    var dimAmount by remember { mutableFloatStateOf(0f) }
    
    // Palette States
    var dominantColor by remember { mutableStateOf<Color?>(null) }
    var vibrantColor by remember { mutableStateOf<Color?>(null) }

    val repository = com.example.aurawallpaper.Graph.wallpaperRepository

    LaunchedEffect(photo.id) {
        repository.dao.isFavorite(photo.id).collect { isFav ->
            isFavorite = isFav
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full screen image with Blur
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.src.original)
                .allowHardware(false)
                .build(),
            contentDescription = photo.alt,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = blurRadius.dp),
            onSuccess = { state ->
                val bitmap = (state.result.drawable as? BitmapDrawable)?.bitmap
                if (bitmap != null) {
                    Palette.from(bitmap).generate { palette ->
                        dominantColor = palette?.dominantSwatch?.rgb?.let { Color(it) }
                        vibrantColor = palette?.vibrantSwatch?.rgb?.let { Color(it) }
                    }
                }
            }
        )
        
        // Dim Overlay
        if (dimAmount > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimAmount))
            )
        }

        // Simulated Overlay
        if (showOverlay) {
            // Mock Clock
            Text(
                text = "10:00",
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            )
            // Mock App Dock
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    )
                }
            }
        }

        // Top Bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Check out this awesome wallpaper I found on Aura Wallpaper! ${photo.url}")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { showCollectionsDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.CollectionsBookmark,
                        contentDescription = "Add to Collection",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { setShowAdjustments(true) }) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Adjust Image",
                        tint = Color.White
                    )
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            if (isFavorite) {
                                repository.dao.deleteFavoriteById(photo.id)
                            } else {
                                repository.dao.insertFavorite(
                                    com.example.aurawallpaper.data.local.FavoriteWallpaper(
                                        id = photo.id,
                                        url = photo.url,
                                        photographer = photo.photographer,
                                        originalUrl = photo.src.original,
                                        photographerUrl = photo.photographerUrl,
                                        altText = photo.alt
                                    )
                                )
                            }
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
                IconButton(onClick = { showOverlay = !showOverlay }) {
                    Icon(
                        imageVector = if (showOverlay) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Overlay",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Bottom Action Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Save Button
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            isSettingWallpaper = true
                            saveToGallery(context, photo, blurRadius, dimAmount)
                            isSettingWallpaper = false
                        }
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                    enabled = !isSettingWallpaper
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                }

                Button(
                    onClick = { setShowBottomSheet(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(48.dp),
                    enabled = !isSettingWallpaper
                ) {
                    if (isSettingWallpaper) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Set Wallpaper")
                    }
                }
            }
        }
    }

    if (showAdjustments) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { setShowAdjustments(false) },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Image Adjustments", style = MaterialTheme.typography.titleLarge)
                
                // Palette Colors
                if (dominantColor != null || vibrantColor != null) {
                    Text("Palette Colors", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        dominantColor?.let { color ->
                            ColorSwatch("Dominant", color)
                        }
                        vibrantColor?.let { color ->
                            ColorSwatch("Vibrant", color)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Blur Slider
                Text("Blur Radius: ${blurRadius.toInt()}")
                Slider(
                    value = blurRadius,
                    onValueChange = { blurRadius = it },
                    valueRange = 0f..24f
                )

                // Dim Slider
                Text("Dim Amount: ${(dimAmount * 100).toInt()}%")
                Slider(
                    value = dimAmount,
                    onValueChange = { dimAmount = it },
                    valueRange = 0f..0.8f
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showBottomSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { setShowBottomSheet(false) },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Set Wallpaper", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))

                BottomSheetOption("Home Screen") {
                    setShowBottomSheet(false)
                    coroutineScope.launch {
                        isSettingWallpaper = true
                        setWallpaper(context, photo.src.original, WallpaperManager.FLAG_SYSTEM, blurRadius, dimAmount)
                        isSettingWallpaper = false
                    }
                }
                BottomSheetOption("Lock Screen") {
                    setShowBottomSheet(false)
                    coroutineScope.launch {
                        isSettingWallpaper = true
                        setWallpaper(context, photo.src.original, WallpaperManager.FLAG_LOCK, blurRadius, dimAmount)
                        isSettingWallpaper = false
                    }
                }
                BottomSheetOption("Home and Lock Screens") {
                    setShowBottomSheet(false)
                    coroutineScope.launch {
                        isSettingWallpaper = true
                        setWallpaper(context, photo.src.original, WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK, blurRadius, dimAmount)
                        isSettingWallpaper = false
                    }
                }
                BottomSheetOption("Crop and Set (System)") {
                    setShowBottomSheet(false)
                    coroutineScope.launch {
                        isSettingWallpaper = true
                        cropAndSetWallpaper(context, photo, blurRadius, dimAmount)
                        isSettingWallpaper = false
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showCollectionsDialog) {
        CollectionsDialog(
            photo = photo,
            repository = repository,
            onDismiss = { showCollectionsDialog = false }
        )
    }
}

@Composable
fun BottomSheetOption(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    )
}

@Composable
fun ColorSwatch(label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color, RoundedCornerShape(8.dp))
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
    }
}

private fun applyEffectsToBitmap(original: Bitmap, blurRadius: Float, dimAmount: Float): Bitmap {
    if (blurRadius == 0f && dimAmount == 0f) return original
    
    // Note: Native Bitmap Blurring without RenderScript is complex. 
    // For this MVP, we will only apply the Dim effect to the actual saved Bitmap. 
    // Blur is applied purely to the UI preview via Compose Modifier.blur.
    
    var result = original
    
    if (dimAmount > 0f) {
        val dimmed = createBitmap(result.width, result.height, result.config ?: Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(dimmed)
        canvas.drawBitmap(result, 0f, 0f, null)
        val paint = android.graphics.Paint()
        paint.color = android.graphics.Color.argb((dimAmount * 255).toInt(), 0, 0, 0)
        canvas.drawRect(0f, 0f, result.width.toFloat(), result.height.toFloat(), paint)
        result = dimmed
    }
    
    return result
}

private suspend fun saveToGallery(context: Context, photo: Photo, blurRadius: Float, dimAmount: Float) {
    withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(photo.src.original)
                .allowHardware(false)
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap
            val processedBitmap = applyEffectsToBitmap(bitmap, blurRadius, dimAmount)

            val uri = ImageUtils.saveBitmapToGallery(context, processedBitmap, "Aura_${photo.id}")
            withContext(Dispatchers.Main) {
                if (uri != null) {
                    Toast.makeText(context, "Saved to gallery!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private suspend fun setWallpaper(context: Context, imageUrl: String, target: Int, blurRadius: Float, dimAmount: Float) {
    withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap
            val processedBitmap = applyEffectsToBitmap(bitmap, blurRadius, dimAmount)

            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.setBitmap(processedBitmap, null, true, target)
            
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private suspend fun cropAndSetWallpaper(context: Context, photo: Photo, blurRadius: Float, dimAmount: Float) {
    withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(photo.src.original)
                .allowHardware(false)
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap
            val processedBitmap = applyEffectsToBitmap(bitmap, blurRadius, dimAmount)

            val uri = ImageUtils.saveBitmapToGallery(context, processedBitmap, "Aura_Temp_${photo.id}")
            withContext(Dispatchers.Main) {
                if (uri != null) {
                    val intent = Intent(WallpaperManager.ACTION_CROP_AND_SET_WALLPAPER).apply {
                        setDataAndType(uri, "image/*")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        Toast.makeText(context, "Device doesn't support system crop", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to load image for cropping", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (_: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to launch crop", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
