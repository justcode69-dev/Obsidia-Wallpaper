package com.example.aurawallpaper.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

object ImageUtils {
    suspend fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String): Uri? {
        return withContext(Dispatchers.IO) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$displayName.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AuraWallpaper")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                var stream: OutputStream? = null
                try {
                    stream = resolver.openOutputStream(it)
                    if (stream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                        resolver.update(it, contentValues, null, null)
                    }
                    it
                } catch (e: Exception) {
                    resolver.delete(it, null, null)
                    e.printStackTrace()
                    null
                } finally {
                    stream?.close()
                }
            }
        }
    }
}
