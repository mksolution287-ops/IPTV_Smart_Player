package com.example.iptvsmarttvplayer.model

data class PlaylistInfo(
    val name: String,
    val url: String,
    val channelCount: Int = 0
)