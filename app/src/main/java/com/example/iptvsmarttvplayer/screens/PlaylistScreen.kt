package com.example.iptvsmarttvplayer.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import com.example.iptvsmarttvplayer.R
import com.example.iptvsmarttvplayer.components.AppBottomNavBar
import com.example.iptvsmarttvplayer.model.PlaylistInfo
import com.example.iptvsmarttvplayer.viewmodel.PlaylistViewModel
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay


// ============================================================
// HARD-CODED APP COLORS
// ============================================================
// These colors intentionally do NOT depend on MaterialTheme.
// This prevents Material 3 / Dynamic Color from overriding
// the application's intended purple and light theme.
// ============================================================

private val AppBackground = Color(0xFFF7F7FA)

private val AppPrimary = Color(0xFF5B00F5)
private val AppPrimaryDark = Color(0xFF4A00C4)
private val AppPrimarySoft = Color(0xFFEDE3FB) // light lavender used behind icons

private val AppText = Color(0xFF111111)
private val AppSecondaryText = Color(0xFF6B7280)

private val AppCard = Color(0xFFFFFFFF)
private val AppUnselected = Color(0xFF9CA3AF)

private val AppTabBackground = Color(0xFFFFFFFF)


// ============================================================
// PLAYLIST SCREEN
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    viewModel: PlaylistViewModel,
    onAddPlaylist: () -> Unit,
    onOpenPlaylist: (PlaylistInfo) -> Unit,
    onNavigateToChannels: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val playlists by viewModel.savedPlaylists.collectAsState()

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var selectedBottomNav by remember {
        mutableIntStateOf(0)
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

                        // App logo
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

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(
                            text = stringResource(R.string.playlist),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = AppText
                            )
                        )
                    }
                },

                actions = {
                    IconButton(
                        onClick = {
                            // Help action
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = stringResource(
                                R.string.the_user_guide
                            ),
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
        // FLOATING ACTION BUTTON
        // ====================================================

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                },

                containerColor = AppPrimary,

                contentColor = Color.White,

                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(
                        R.string.add_playlist
                    )
                )
            }
        },

        // ====================================================
        // CUSTOM BOTTOM NAVIGATION BAR
        // ====================================================

        bottomBar = {
            AppBottomNavBar(
                selectedIndex = selectedBottomNav,
                onItemSelected = { index ->
                    selectedBottomNav = index
                    when (index) {
                        0 -> Unit // already on Playlists
                        1 -> onNavigateToChannels()
                        2 -> onNavigateToSettings()
                    }
                }
            )
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ====================================================
            // BANNER / CAROUSEL
            // ====================================================

            BannerCarousel()

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // ====================================================
            // FILTER TABS
            // ====================================================

            Box(
                contentAlignment = Alignment.Center
            ) {
                FilterTabs(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // ====================================================
            // PLAYLIST LIST
            // ====================================================

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp),

                contentPadding = PaddingValues(
                    bottom = 88.dp
                )
            ) {

                // Playlist cards
                items(playlists) { playlist ->

                    PlaylistCard(
                        playlist = playlist,

                        onClick = {
                            onOpenPlaylist(playlist)
                        },

                        onDelete = {
                            viewModel.deletePlaylist(playlist)
                        }
                    )
                }

                // Welcome card
                item {
                    WelcomeCard(
                        onStartClick = {
                            // Open guide
                        }
                    )
                }
            }
        }
    }

    // ============================================================
    // ADD PLAYLIST DIALOG
    // ============================================================

    if (showAddDialog) {

        AddPlaylistDialog(

            onDismiss = {
                showAddDialog = false
            },

            onImportUrl = {
                showAddDialog = false
                onAddPlaylist()
            },

            onUploadM3u = {
                showAddDialog = false
            },

            onLoadFromDevice = {
                showAddDialog = false
            },

            onPlaySingleStream = {
                showAddDialog = false
            }
        )
    }
}


// ============================================================
// BANNER CAROUSEL
// ============================================================

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BannerCarousel() {

    val pageCount = 3

    val pagerState = rememberPagerState(
        pageCount = {
            pageCount
        }
    )

    // ============================================================
    // AUTO-SCROLL
    // ============================================================
    // Advances to the next page every few seconds, wrapping back
    // to the first page after the last one. Pauses implicitly
    // while the user is dragging, since animateScrollToPage calls
    // simply queue up behind any in-progress user gesture.
    // ============================================================

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)

            val nextPage = (pagerState.currentPage + 1) % pageCount

            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HorizontalPager(
            state = pagerState,

            contentPadding = PaddingValues(
                horizontal = 40.dp
            ),

            pageSpacing = 12.dp,

            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) { page ->

            // ========================================================
            // CENTER-PAGE ENLARGE EFFECT
            // ========================================================
            // pageOffset is 0 for the currently centered page and
            // grows toward 1 (or -1) for neighboring pages. We use
            // it to scale + fade adjacent pages down slightly, so
            // the active/center banner reads as "enlarged" relative
            // to the ones peeking in on either side.
            // ========================================================

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pageOffset =
                            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                                .absoluteValue
                                .coerceIn(0f, 1f)

                        val scale = lerp(0.85f, 1f, 1f - pageOffset)
                        val alpha = lerp(0.6f, 1f, 1f - pageOffset)

                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    },

                contentAlignment = Alignment.Center
            ) {

                Card(
                    shape = RoundedCornerShape(20.dp),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),

                    modifier = Modifier.fillMaxSize()
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFF1ECFA),
                                        Color(0xFFE4D9F7)
                                    )
                                )
                            ),

                        contentAlignment = Alignment.Center
                    ) {

                        // Replace this placeholder with your actual
                        // banner image when available, e.g.:
                        // Image(
                        //     painter = painterResource(R.drawable.banner_living_room),
                        //     contentDescription = null,
                        //     contentScale = ContentScale.Crop,
                        //     modifier = Modifier.fillMaxSize()
                        // )

                        Icon(
                            imageVector = Icons.Outlined.Tv,

                            contentDescription = null,

                            tint = AppPrimary.copy(
                                alpha = 0.6f
                            ),

                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // ========================================================
        // PAGE INDICATORS
        // ========================================================

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            repeat(3) { index ->

                Box(
                    modifier = Modifier
                        .size(
                            width = if (
                                pagerState.currentPage == index
                            ) {
                                18.dp
                            } else {
                                6.dp
                            },

                            height = 6.dp
                        )
                        .clip(
                            RoundedCornerShape(3.dp)
                        )
                        .background(
                            if (
                                pagerState.currentPage == index
                            ) {
                                AppPrimary
                            } else {
                                Color(0xFFD1D5DB)
                            }
                        )
                )
            }
        }
    }
}


// ============================================================
// FILTER TABS
// ============================================================

@Composable
private fun FilterTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {

    val tabs = listOf(

        Triple(
            0,
            stringResource(R.string.all),
            Icons.Default.Apps
        ),

        Triple(
            1,
            stringResource(R.string.url),
            Icons.Outlined.Link
        ),

        Triple(
            2,
            stringResource(R.string.file),
            Icons.Outlined.Folder
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            10.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        tabs.forEach { (index, label, icon) ->

            val selected = selectedTab == index

            Surface(

                onClick = {
                    onTabSelected(index)
                },

                // Fully rounded capsule shape, matching the design.
                shape = RoundedCornerShape(50),

                // HARD-CODED COLORS
                color = if (selected) {
                    AppPrimary
                } else {
                    AppTabBackground
                },

                shadowElevation = if (selected) {
                    0.dp
                } else {
                    1.dp
                },

                modifier = Modifier.height(40.dp)
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = icon,

                        contentDescription = null,

                        tint = if (selected) {
                            Color.White
                        } else {
                            AppText
                        },

                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = label,

                        color = if (selected) {
                            Color.White
                        } else {
                            AppText
                        },

                        style = MaterialTheme.typography.labelLarge,

                        fontWeight = if (selected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            }
        }
    }
}


// ============================================================
// PLAYLIST CARD
// ============================================================

@Composable
private fun PlaylistCard(
    playlist: PlaylistInfo,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Card(
        onClick = onClick,

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = AppCard
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),

        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // ====================================================
            // PLAYLIST ICON
            // ====================================================
            // Light lavender background with a purple clapperboard
            // icon, matching the reference design.
            // ====================================================

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(AppPrimarySoft),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Theaters,

                    contentDescription = null,

                    tint = AppPrimary,

                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            // ====================================================
            // PLAYLIST INFORMATION
            // ====================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = playlist.name,

                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AppText
                    )
                )

                Text(
                    text = stringResource(
                        R.string.channels_count,
                        playlist.channelCount
                    ),

                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AppSecondaryText
                    )
                )
            }

            // ====================================================
            // MORE MENU
            // ====================================================

            Box {

                IconButton(
                    onClick = {
                        menuExpanded = true
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.MoreVert,

                        contentDescription = null,

                        tint = AppSecondaryText
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,

                    onDismissRequest = {
                        menuExpanded = false
                    }
                ) {

                    DropdownMenuItem(

                        text = {
                            Text("Delete")
                        },

                        onClick = {

                            menuExpanded = false

                            onDelete()
                        }
                    )
                }
            }
        }
    }
}


// ============================================================
// WELCOME CARD
// ============================================================

@Composable
private fun WelcomeCard(
    onStartClick: () -> Unit
) {

    Card(
        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = AppCard
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),

        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = stringResource(
                        R.string.welcome_to_iptv_smart
                    ),

                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppText
                    )
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = stringResource(
                        R.string.the_user_guide
                    ),

                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppSecondaryText
                    )
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // =================================================
                // START BUTTON
                // =================================================

                Surface(
                    onClick = onStartClick,

                    shape = RoundedCornerShape(24.dp),

                    color = AppPrimary
                ) {

                    Text(
                        text = stringResource(
                            R.string.start
                        ),

                        color = Color.White,

                        fontWeight = FontWeight.SemiBold,

                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        )
                    )
                }
            }

            // ====================================================
            // DECORATIVE TV / REMOTE GRAPHIC
            // ====================================================

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(
                        R.drawable.entertainment
                    ),

                    contentDescription = null,

                    contentScale = ContentScale.Crop,

                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


// ============================================================
// ADD PLAYLIST DIALOG
// ============================================================

@Composable
private fun AddPlaylistDialog(
    onDismiss: () -> Unit,
    onImportUrl: () -> Unit,
    onUploadM3u: () -> Unit,
    onLoadFromDevice: () -> Unit,
    onPlaySingleStream: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),

            color = AppCard,

            tonalElevation = 8.dp
        ) {

            Column(
                modifier = Modifier.padding(
                    vertical = 8.dp
                )
            ) {

                Text(
                    text = stringResource(
                        R.string.add_playlist
                    ),

                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AppText
                    ),

                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    )
                )

                DialogOption(
                    icon = Icons.Outlined.Link,

                    label = stringResource(
                        R.string.import_playlist_url
                    ),

                    onClick = onImportUrl
                )

                DialogOption(
                    icon = Icons.Outlined.Folder,

                    label = stringResource(
                        R.string.upload_m3u_file
                    ),

                    onClick = onUploadM3u
                )

                DialogOption(
                    icon = Icons.Outlined.Tv,

                    label = stringResource(
                        R.string.load_from_device
                    ),

                    onClick = onLoadFromDevice
                )

                DialogOption(
                    icon = Icons.Default.PlayArrow,

                    label = stringResource(
                        R.string.play_single_stream
                    ),

                    onClick = onPlaySingleStream
                )
            }
        }
    }
}


// ============================================================
// DIALOG OPTION
// ============================================================

@Composable
private fun DialogOption(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(
                horizontal = 20.dp,
                vertical = 14.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,

            contentDescription = null,

            tint = AppSecondaryText,

            modifier = Modifier.size(22.dp)
        )

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Text(
            text = label,

            style = MaterialTheme.typography.bodyLarge.copy(
                color = AppText
            )
        )
    }
}


// ============================================================
// The custom bottom nav bar now lives in a shared component:
// see com.example.iptvsmarttvplayer.components.AppBottomNavBar.
// It's reused as-is by ChannelListScreen (and any future
// bottom-nav-level screen) so the bar can never drift out of
// sync between screens.