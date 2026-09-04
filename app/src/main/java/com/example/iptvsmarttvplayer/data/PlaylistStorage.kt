package com.example.iptvsmarttvplayer.data

import android.content.Context
import com.example.iptvsmarttvplayer.model.PlaylistInfo
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persists the list of playlists the user has imported so they show up again
 * next time the app is opened (see the "Playlist" screen).
 */
class PlaylistStorage(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getPlaylists(): List<PlaylistInfo> {
        val json = prefs.getString(KEY_PLAYLISTS, null) ?: return emptyList()
        val array = JSONArray(json)
        val result = mutableListOf<PlaylistInfo>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(
                PlaylistInfo(
                    name = obj.getString("name"),
                    url = obj.getString("url"),
                    channelCount = obj.optInt("channelCount", 0)
                )
            )
        }
        return result
    }

    fun savePlaylists(playlists: List<PlaylistInfo>) {
        val array = JSONArray()
        playlists.forEach { info ->
            val obj = JSONObject()
            obj.put("name", info.name)
            obj.put("url", info.url)
            obj.put("channelCount", info.channelCount)
            array.put(obj)
        }
        prefs.edit().putString(KEY_PLAYLISTS, array.toString()).apply()
    }

    fun addOrUpdatePlaylist(info: PlaylistInfo) {
        val current = getPlaylists().toMutableList()
        val existingIndex = current.indexOfFirst { it.url == info.url }
        if (existingIndex >= 0) {
            current[existingIndex] = info
        } else {
            current.add(info)
        }
        savePlaylists(current)
    }

    fun deletePlaylist(info: PlaylistInfo) {
        val current = getPlaylists().toMutableList()
        current.removeAll { it.url == info.url }
        savePlaylists(current)
    }

    companion object {
        private const val PREFS_NAME = "iptv_player_prefs"
        private const val KEY_PLAYLISTS = "saved_playlists"
    }
}