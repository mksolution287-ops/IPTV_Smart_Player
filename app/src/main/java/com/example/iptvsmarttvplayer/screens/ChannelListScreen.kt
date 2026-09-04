package com.example.iptvsmarttvplayer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.iptvsmarttvplayer.R
import com.example.iptvsmarttvplayer.components.AppBottomNavBar
import com.example.iptvsmarttvplayer.components.BottomNavTab
import com.example.iptvsmarttvplayer.model.Channel
import com.example.iptvsmarttvplayer.viewmodel.PlaylistViewModel
import kotlin.math.absoluteValue


// ============================================================
// HARD-CODED APP COLORS
// ============================================================
// Kept file-local and independent of MaterialTheme, matching the
// convention used across the rest of the app's screens.
// ============================================================

private val AppBackground = Color(0xFFF7F7FA)

private val AppPrimary = Color(0xFF5B00F5)
private val AppText = Color(0xFF111111)
private val AppSecondaryText = Color(0xFF6B7280)

private val AppCard = Color(0xFFFFFFFF)
private val AppTabBackground = Color(0xFFFFFFFF)
private val AppBorder = Color(0xFFE5E7EB)


// ============================================================
// CHANNELS SCREEN
// ============================================================
// NOTE: this screen assumes the following fields exist on your
// `Channel` model (nullable is fine): `description: String?`,
// `category: String?`, `quality: String?`, and
// `thumbnailUrl: String?`. Rename the accessors below if your
// actual property names differ.
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelListScreen(
    viewModel: PlaylistViewModel,
    onChannelClick: (Int) -> Unit,
    onNavigateToPlaylists: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val channels by viewModel.filteredChannels.collectAsState()
    val allChannels by viewModel.channels.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    // Simple in-memory favorites for this session, keyed by channel index
    // in the full list. Swap for a persisted favorites source if you have one.
    var favoriteIndices by remember { mutableStateOf(setOf<Int>()) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 All, 1 Recent, 2 Favorites
    var isGridView by remember { mutableStateOf(false) }

    val visibleChannels = when (selectedTab) {
        2 -> channels.filter { favoriteIndices.contains(allChannels.indexOf(it)) }
        // TODO: wire this up to real "recently played" tracking once you have it.
        1 -> channels
        else -> channels
    }

    Scaffold(
        containerColor = AppBackground,

        // ====================================================
        // TOP APP BAR
        // ====================================================

        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Static "Channels" title — unlike SpecificChannelList,
                        // this screen is not scoped to a single playlist, so it
                        // does not show a dynamic playlist name here.
                        Text(
                            text = stringResource(R.string.channels),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = AppText
                            )
                        )
                    }
                },

                actions = {
                    IconButton(onClick = { /* Help action */ }) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = stringResource(R.string.the_user_guide),
                            tint = AppPrimary
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBackground
                )
            )
        },

        // ====================================================
        // CUSTOM BOTTOM NAVIGATION BAR
        // ====================================================

        bottomBar = {
            AppBottomNavBar(
                selectedIndex = BottomNavTab.CHANNELS,
                onItemSelected = { index ->
                    when (index) {
                        BottomNavTab.PLAYLISTS -> onNavigateToPlaylists()
                        BottomNavTab.SETTINGS -> onNavigateToSettings()
                    }
                }
            )
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(4.dp))

            // ====================================================
            // FILTER TABS: All / Recent / Favorites
            // ====================================================

            ChannelFilterTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ====================================================
            // SEARCH BAR + GRID TOGGLE
            // ====================================================

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search Channel") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = AppSecondaryText
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppPrimary,
                        unfocusedBorderColor = AppBorder,
                        focusedContainerColor = AppCard,
                        unfocusedContainerColor = AppCard
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                )

                // Grid / list view toggle button.
                Surface(
                    onClick = { isGridView = !isGridView },
                    shape = RoundedCornerShape(16.dp),
                    color = AppCard,
                    shadowElevation = 1.dp,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = AppText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ====================================================
            // CHANNEL LIST
            // ====================================================
            // TODO: when isGridView is true, swap this LazyColumn for a
            // LazyVerticalGrid using the same ChannelRow content.

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(visibleChannels) { channel ->
                    val originalIndex = allChannels.indexOf(channel)

                    ChannelRow(
                        channel = channel,
                        isFavorite = favoriteIndices.contains(originalIndex),
                        onClick = { onChannelClick(originalIndex) },
                        onFavoriteToggle = {
                            favoriteIndices = if (favoriteIndices.contains(originalIndex)) {
                                favoriteIndices - originalIndex
                            } else {
                                favoriteIndices + originalIndex
                            }
                        }
                    )
                }
            }
        }
    }
}


// ============================================================
// FILTER TABS
// ============================================================

@Composable
private fun ChannelFilterTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {

    val tabs = listOf(
        Triple(0, stringResource(R.string.all), Icons.Default.Apps),
        Triple(1, stringResource(R.string.recent), Icons.Default.History),
        Triple(2, stringResource(R.string.favorites), Icons.Default.Favorite)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {

        tabs.forEach { (index, label, icon) ->

            val selected = selectedTab == index

            Surface(
                onClick = { onTabSelected(index) },
                shape = RoundedCornerShape(50),
                color = if (selected) AppPrimary else AppTabBackground,
                shadowElevation = if (selected) 0.dp else 1.dp,
                modifier = Modifier.height(40.dp)
            ) {

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        // The Favorites heart stays black even when unselected,
                        // matching the reference design.
                        tint = if (selected) Color.White else AppText,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = label,
                        color = if (selected) Color.White else AppText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}


// ============================================================
// CHANNEL ROW
// ============================================================

@Composable
private fun ChannelRow(
    channel: Channel,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ChannelThumbnail(channel = channel)

            Spacer(modifier = Modifier.width(12.dp))

            // ====================================================
            // NAME / DESCRIPTION / CATEGORY
            // ====================================================

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AppText
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

//                val description = channel.description
//                if (!description.isNullOrBlank()) {
//                    Text(
//                        text = description,
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            color = AppSecondaryText
//                        ),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }

//                val category = channel.category
//                if (!category.isNullOrBlank()) {
//                    Text(
//                        text = category,
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            fontWeight = FontWeight.Medium,
//                            color = categoryColor(category)
//                        ),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // ====================================================
            // FAVORITE TOGGLE
            // ====================================================

            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (isFavorite) AppPrimary else AppSecondaryText
                )
            }
        }
    }
}


// ============================================================
// CHANNEL THUMBNAIL
// ============================================================
// Loads channel.thumbnailUrl with Coil. Falls back to a letter
// avatar while there's no URL. A small colored quality badge
// (HD / FHD / 4K UHD / VOD / PREMIERE / ...) overlaps the bottom
// edge, matching the reference design.
// ============================================================

@Composable
private fun ChannelThumbnail(channel: Channel) {

    val thumbnailUrl = channel.thumbnailUrl

    Box(
        modifier = Modifier.size(width = 56.dp, height = 60.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(thumbnailFallbackColor(channel.name)),
            contentAlignment = Alignment.Center
        ) {

            if (!thumbnailUrl.isNullOrBlank()) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = channel.name,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = channel.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}


// ============================================================
// COLOR HELPERS
// ============================================================

private val fallbackPalette = listOf(
    Color(0xFFF59E0B), // amber
    Color(0xFFEF4444), // red
    Color(0xFF111111), // near-black
    Color(0xFF10B981), // green
    Color(0xFF8B5CF6), // violet
    Color(0xFF3B82F6), // blue
)

private fun thumbnailFallbackColor(name: String): Color {
    val index = name.hashCode().absoluteValue % fallbackPalette.size
    return fallbackPalette[index]
}

private fun qualityBadgeColor(quality: String): Color = when (quality.trim().uppercase()) {
    "VOD" -> Color(0xFFF59E0B)
    "HD" -> Color(0xFF3B82F6)
    "FHD" -> Color(0xFF15803D)
    "4K", "4K UHD", "UHD" -> Color(0xFF10B981)
    "PREMIERE" -> Color(0xFF8B5CF6)
    else -> Color(0xFF6B7280)
}

private val categoryPalette = listOf(
    Color(0xFF2563EB), // blue
    Color(0xFFDC2626), // red
    Color(0xFF16A34A), // green
    Color(0xFF7C3AED), // purple
    Color(0xFFD97706), // amber
)

private fun categoryColor(category: String): Color {
    val normalized = category.trim().lowercase()
    return when {
        "news" in normalized -> Color(0xFFDC2626)
        "sport" in normalized -> Color(0xFF16A34A)
        "movie" in normalized || "cinema" in normalized -> Color(0xFF7C3AED)
        "entertain" in normalized -> Color(0xFF2563EB)
        "info" in normalized -> Color(0xFF2563EB)
        else -> categoryPalette[normalized.hashCode().absoluteValue % categoryPalette.size]
    }
}