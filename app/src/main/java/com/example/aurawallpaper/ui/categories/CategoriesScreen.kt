package com.example.aurawallpaper.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val categories = listOf(
    "Animals", "Anime", "Bollywood", "Logos", 
    "Cars & Vehicles", "Designs", "Drawings", "Entertainment", "Funny", 
    "Games", "Holidays", "Love", "Music", "Nature", "News & Politics", 
    "Other", "Patterns", "Sayings", "Spiritual", "Sport", "Technology", 
    "Space", "Comics"
)

val categoryColors = listOf(
    Color(0xFFE53935), Color(0xFFD81B60), Color(0xFF8E24AA), Color(0xFF5E35B1),
    Color(0xFF3949AB), Color(0xFF1E88E5), Color(0xFF039BE5), Color(0xFF00ACC1),
    Color(0xFF00897B), Color(0xFF43A047), Color(0xFF7CB342), Color(0xFFC0CA33),
    Color(0xFFFDD835), Color(0xFFFFB300), Color(0xFFFB8C00), Color(0xFFF4511E)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(onCategoryClick: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Categories") })
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                val color1 = categoryColors[(category.hashCode() and 0x7FFFFFFF) % categoryColors.size]
                val color2 = categoryColors[((category.hashCode() and 0x7FFFFFFF) / 3) % categoryColors.size]
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(color1, color2)
                            )
                        )
                        .clickable { onCategoryClick(category) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
