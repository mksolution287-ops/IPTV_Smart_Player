package com.example.iptvsmarttvplayer.data

import android.content.Context
import org.json.JSONArray

/**
 * Stores favorite channels (by stream URL, since that's the one guaranteed-unique
 * field we have) and a most-recently-played list, so both survive app restarts.
 */
class FavoritesStorage(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getFavorites(): Set<String> = readStringList(KEY_FAVORITES).toSet()

    fun setFavorites(urls: Set<String>) {
        writeStringList(KEY_FAVORITES, urls.toList())
    }

    fun getRecents(): List<String> = readStringList(KEY_RECENTS)

    fun setRecents(urls: List<String>) {
        writeStringList(KEY_RECENTS, urls)
    }

    private fun readStringList(key: String): List<String> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val array = JSONArray(json)
        return (0 until array.length()).map { array.getString(it) }
    }

    private fun writeStringList(key: String, values: List<String>) {
        val array = JSONArray()
        values.forEach { array.put(it) }
        prefs.edit().putString(key, array.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "iptv_player_favorites"
        private const val KEY_FAVORITES = "favorite_urls"
        private const val KEY_RECENTS = "recent_urls"

        const val MAX_RECENTS = 30
    }
}