package com.example.iptvsmarttvplayer.model

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import coil3.Image

data class Channel(
    val name: String,
    val url: String,
    val logoUrl: String? = null,
    val group: String? = null,
    val thumbnailUrl: String? = null
)