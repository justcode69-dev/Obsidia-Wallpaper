package com.example.aurawallpaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.aurawallpaper.theme.AuraWallpaperTheme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      var isDarkTheme by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
      AuraWallpaperTheme(darkTheme = isDarkTheme) { 
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { 
          Box(modifier = Modifier.fillMaxSize()) {
            MainNavigation()
            
            // Theme toggle floating button
            androidx.compose.material3.FloatingActionButton(
                onClick = { isDarkTheme = !isDarkTheme },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp, end = 16.dp)
            ) {
                Text(
                    text = if (isDarkTheme) "Light" else "Dark",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
          }
        } 
      }
    }
  }
}
