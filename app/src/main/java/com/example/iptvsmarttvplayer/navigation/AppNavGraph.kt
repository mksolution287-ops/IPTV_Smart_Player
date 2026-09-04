package com.example.iptvsmarttvplayer.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iptvsmarttvplayer.screens.ChannelListScreen
import com.example.iptvsmarttvplayer.screens.ImportPlaylistScreen
import com.example.iptvsmarttvplayer.screens.PlayerScreen
import com.example.iptvsmarttvplayer.screens.PlaylistsScreen
import com.example.iptvsmarttvplayer.screens.SpecificChannelList
// TODO: point this at your real Settings screen composable once it exists.
// import com.example.iptvsmarttvplayer.screens.SettingsScreen
import com.example.iptvsmarttvplayer.viewmodel.PlaylistViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: PlaylistViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = Screen.Playlists.route) {

        composable(Screen.Playlists.route) {
            PlaylistsScreen(
                viewModel = viewModel,
                onAddPlaylist = { navController.navigate(Screen.Import.route) },
                onOpenPlaylist = { info ->
                    viewModel.openSavedPlaylist(info) {
                        navController.navigate(Screen.SpecificChannelList.route)
                    }
                },

                // ============================================================
                // TAB-STYLE NAVIGATION
                // ============================================================
                // launchSingleTop avoids piling up duplicate destinations when
                // a bottom nav item is tapped repeatedly, and popUpTo trims the
                // back stack back to the start destination (Playlists) so
                // switching tabs behaves like a standard bottom navigation bar
                // instead of pushing an ever-growing back stack.
                // ============================================================

                onNavigateToChannels = {
                    navController.navigate(Screen.Channels.route) {
                        popUpTo(Screen.Playlists.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(Screen.Playlists.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.Import.route) {
            ImportPlaylistScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onImported = {
                    navController.popBackStack()
                    navController.navigate(Screen.Channels.route)
                }
            )
        }

        // ================================================================
        // CHANNELS (bottom-nav tab — matches the "Channels" screenshot:
        // static title, All / Recent / Favorites tabs, search + thumbnails)
        // ================================================================

        composable(Screen.Channels.route) {
            ChannelListScreen(
                viewModel = viewModel,
                onChannelClick = { index ->
                    viewModel.selectChannel(index)
                    navController.navigate(Screen.Player.route)
                },
                onNavigateToPlaylists = {
                    navController.navigate(Screen.Playlists.route) {
                        popUpTo(Screen.Playlists.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(Screen.Playlists.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // ================================================================
        // SPECIFIC CHANNEL LIST (pushed screen for a single opened
        // playlist — keeps its own dynamic top-bar title, unlike the
        // Channels tab above)
        // ================================================================

        composable(Screen.SpecificChannelList.route) {
            SpecificChannelList(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onChannelClick = { index ->
                    viewModel.selectChannel(index)
                    navController.navigate(Screen.Player.route)
                }
            )
        }

        composable(Screen.Player.route) {
            PlayerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ================================================================
        // SETTINGS
        // ================================================================
        // Add Screen.Settings to your Screen sealed class (e.g. route =
        // "settings") and swap in your real SettingsScreen composable here.
        // Left as a stub so the bottom nav's third tab has somewhere to go.
        // ================================================================

        composable(Screen.Settings.route) {
            // SettingsScreen(
            //     viewModel = viewModel,
            //     onBack = { navController.popBackStack() }
            // )
        }
    }
}