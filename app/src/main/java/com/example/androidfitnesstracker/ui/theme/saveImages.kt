package com.example.androidfitnesstracker.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

fun saveImageToInternalStorage(context: Context, uri: Uri): Int? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "image_${System.currentTimeMillis()}.png" // Unique name
        val file = File(context.filesDir, fileName)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        fileName.hashCode() // Simulate an integer ID based on file name
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getImageFromInternalStorage(context: Context, resourceId: Int): Bitmap? {
    val fileName = "image_${resourceId}.png" // Reverse the hash logic
    val file = File(context.filesDir, fileName)
    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)
    } else {
        null // Handle missing images
    }
}

