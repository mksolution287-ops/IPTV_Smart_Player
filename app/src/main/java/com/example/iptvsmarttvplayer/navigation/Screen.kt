package com.example.iptvsmarttvplayer.navigation

sealed class Screen(val route: String) {
    object Playlists : Screen("playlists")
    object Import : Screen("import")
    object Channels : Screen("channels")
    object Player : Screen("player")
    object Settings : Screen("settings")
    object SpecificChannelList : Screen("specificChannelList")
}