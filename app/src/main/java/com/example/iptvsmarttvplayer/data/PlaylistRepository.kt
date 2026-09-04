package com.example.iptvsmarttvplayer.data

import com.example.iptvsmarttvplayer.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Fetches a remote M3U playlist and parses it into a list of [Channel]s.
 */
class PlaylistRepository {

    suspend fun loadPlaylist(url: String): Result<List<Channel>> = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.requestMethod = "GET"
            connection.instanceFollowRedirects = true

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                connection.disconnect()
                return@withContext Result.failure(
                    Exception("Failed to fetch playlist: HTTP $responseCode")
                )
            }

            val content = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val channels = M3uParser.parse(content)
            if (channels.isEmpty()) {
                Result.failure(Exception("No channels found in this playlist"))
            } else {
                Result.success(channels)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not load playlist: ${e.message}", e))
        }
    }
}