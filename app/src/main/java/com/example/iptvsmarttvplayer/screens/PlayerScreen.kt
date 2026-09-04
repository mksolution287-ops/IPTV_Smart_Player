package com.example.iptvsmarttvplayer.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.iptvsmarttvplayer.viewmodel.PlaylistViewModel
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun PlayerScreen(
    viewModel: PlaylistViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val channels by viewModel.channels.collectAsState()
    val selectedIndex by viewModel.selectedChannelIndex.collectAsState()
    val channel = channels.getOrNull(selectedIndex)

    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var isFavorite by remember { mutableStateOf(false) }
    var controlsLocked by remember { mutableStateOf(false) }

    var brightness by remember {
        mutableStateOf(
            activity?.window?.attributes?.screenBrightness
                ?.takeIf { it in 0f..1f } ?: 0.5f
        )
    }
    var volume by remember { mutableStateOf(exoPlayer.volume) }

    // (Re)load media whenever the selected channel changes.
    LaunchedEffect(channel?.url) {
        val url = channel?.url ?: return@LaunchedEffect
        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    // Track play/pause + duration changes.
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onEvents(player: Player, events: Player.Events) {
                duration = player.duration.coerceAtLeast(0L)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Poll the current playback position for the progress label.
    LaunchedEffect(exoPlayer) {
        while (true) {
            currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
            delay(500)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top bar: back, channel name, favorite
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
            ) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = channel?.name ?: "",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { isFavorite = !isFavorite }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White
                )
            }
        }

        if (!controlsLocked) {
            // Middle row: brightness slider, playback controls, volume slider
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VerticalIconSlider(
                    icon = Icons.Default.WbSunny,
                    value = brightness,
                    onValueChange = { newValue ->
                        brightness = newValue
                        activity?.window?.let { window ->
                            val params = window.attributes
                            params.screenBrightness = newValue
                            window.attributes = params
                        }
                    }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.playPreviousChannel() },
                        enabled = selectedIndex > 0
                    ) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledIconButton(
                        onClick = { if (isPlaying) exoPlayer.pause() else exoPlayer.play() },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause"
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { viewModel.playNextChannel() },
                        enabled = selectedIndex < channels.lastIndex
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                VerticalIconSlider(
                    icon = Icons.Default.VolumeUp,
                    value = volume,
                    onValueChange = { newValue ->
                        volume = newValue
                        exoPlayer.volume = newValue
                    }
                )
            }
        }

        // Bottom bar: position/duration + lock toggle
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { controlsLocked = !controlsLocked },
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            ) {
                Icon(
                    imageVector = if (controlsLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Lock controls",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun VerticalIconSlider(
    icon: ImageVector,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .height(140.dp)
                .width(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..1f,
                modifier = Modifier
                    .width(140.dp)
                    .rotate(270f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Icon(icon, contentDescription = null, tint = Color.White)
    }
}

private fun formatTime(millis: Long): String {
    if (millis <= 0) return "00:00"
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}