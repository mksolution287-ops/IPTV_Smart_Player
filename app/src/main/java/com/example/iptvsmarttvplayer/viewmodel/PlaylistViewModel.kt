package com.example.iptvsmarttvplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.iptvsmarttvplayer.data.PlaylistRepository
import com.example.iptvsmarttvplayer.data.PlaylistStorage
import com.example.iptvsmarttvplayer.model.Channel
import com.example.iptvsmarttvplayer.model.PlaylistInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.filter

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlaylistRepository()
    private val storage = PlaylistStorage(application)

    private val _savedPlaylists = MutableStateFlow<List<PlaylistInfo>>(emptyList())
    val savedPlaylists: StateFlow<List<PlaylistInfo>> = _savedPlaylists.asStateFlow()

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _currentPlaylistName = MutableStateFlow("")
    val currentPlaylistName: StateFlow<String> = _currentPlaylistName.asStateFlow()

    private val _selectedChannelIndex = MutableStateFlow(0)
    val selectedChannelIndex: StateFlow<Int> = _selectedChannelIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredChannels: StateFlow<List<Channel>> =
        combine(_channels, _searchQuery) { channels, query ->
            if (query.isBlank()) {
                channels
            } else {
                channels.filter { it.name.contains(query, ignoreCase = true) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        _savedPlaylists.value = storage.getPlaylists()
    }

    /** Downloads + parses the playlist at [url] and, on success, loads it as the active channel list. */
    fun importPlaylist(name: String, url: String, onSuccess: () -> Unit) {
        if (url.isBlank()) {
            _errorMessage.value = "Please enter a playlist URL"
            return
        }

        val playlistName = name.ifBlank { "Playlist" }
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repository.loadPlaylist(url.trim())
            _isLoading.value = false

            result.onSuccess { channelList ->
                _channels.value = channelList
                _currentPlaylistName.value = playlistName
                _selectedChannelIndex.value = 0
                _searchQuery.value = ""

                val info = PlaylistInfo(
                    name = playlistName,
                    url = url.trim(),
                    channelCount = channelList.size
                )
                storage.addOrUpdatePlaylist(info)
                _savedPlaylists.value = storage.getPlaylists()

                onSuccess()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Failed to load playlist"
            }
        }
    }

    fun openSavedPlaylist(info: PlaylistInfo, onSuccess: () -> Unit) {
        importPlaylist(info.name, info.url, onSuccess)
    }

    fun deletePlaylist(info: PlaylistInfo) {
        storage.deletePlaylist(info)
        _savedPlaylists.value = storage.getPlaylists()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectChannel(index: Int) {
        if (index in _channels.value.indices) {
            _selectedChannelIndex.value = index
        }
    }

    fun playNextChannel() {
        val next = _selectedChannelIndex.value + 1
        if (next < _channels.value.size) {
            _selectedChannelIndex.value = next
        }
    }

    fun playPreviousChannel() {
        val prev = _selectedChannelIndex.value - 1
        if (prev >= 0) {
            _selectedChannelIndex.value = prev
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}