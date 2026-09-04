package com.example.iptvsmarttvplayer.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.iptvsmarttvplayer.R

// ============================================================
// SHARED APP COLORS FOR THIS COMPONENT
// ============================================================
// Kept local/hard-coded on purpose (same convention used across
// the app's screens) so this bar never drifts from the Material
// theme by accident.
// ============================================================

private val AppPrimary = Color(0xFF5B00F5)
private val AppUnselected = Color(0xFF9CA3AF)

/**
 * Index of each tab in [AppBottomNavBar], for readability at call sites.
 */
object BottomNavTab {
    const val PLAYLISTS = 0
    const val CHANNELS = 1
    const val SETTINGS = 2
}

private data class BottomNavItem(
    val label: String,
    val icon: ImageVector
)

// ============================================================
// CUSTOM BOTTOM NAVIGATION BAR
// ============================================================
// Fully custom (no Material NavigationBar / NavigationBarItem).
// The selected item renders as a purple "pill" with the icon
// and label side by side in white. Unselected items render as
// a plain icon-over-label stack in gray. Shared by every root
// (bottom-nav-level) screen so the bar is pixel-identical and
// stays in sync as tabs are added.
// ============================================================

@Composable
fun AppBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {

    val items = listOf(
        BottomNavItem(
            label = stringResource(R.string.playlists),
            icon = Icons.Default.PlayArrow
        ),
        BottomNavItem(
            label = stringResource(R.string.channels),
            icon = Icons.Default.Tv
        ),
        BottomNavItem(
            label = stringResource(R.string.settings),
            icon = Icons.Default.Settings
        )
    )

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),

            horizontalArrangement = Arrangement.SpaceEvenly,

            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEachIndexed { index, item ->

                val selected = selectedIndex == index

                if (selected) {

                    // --------------------------------------------
                    // SELECTED: purple pill, icon + label inline
                    // --------------------------------------------

                    Surface(
                        onClick = {
                            onItemSelected(index)
                        },

                        shape = RoundedCornerShape(50),

                        color = AppPrimary,

                        modifier = Modifier.height(44.dp)
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 18.dp,
                                vertical = 10.dp
                            ),

                            verticalAlignment = Alignment.CenterVertically,

                            horizontalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                imageVector = item.icon,

                                contentDescription = item.label,

                                tint = Color.White,

                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = item.label,

                                color = Color.White,

                                style = MaterialTheme.typography.labelLarge,

                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                } else {

                    // --------------------------------------------
                    // UNSELECTED: plain icon over label, gray
                    // --------------------------------------------

                    Column(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onItemSelected(index)
                                }
                            )
                            .padding(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            ),

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = item.icon,

                            contentDescription = item.label,

                            tint = AppUnselected,

                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = item.label,

                            color = AppUnselected,

                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}