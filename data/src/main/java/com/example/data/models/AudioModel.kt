package com.example.data.models

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

data class AudioModel (
    val logo: Bitmap,
    val name: String,
    val duration: Int,
    val audioData: String,
    val audioAlbum: Int
)