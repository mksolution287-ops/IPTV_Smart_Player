package com.example.iptvsmarttvplayer.data

import com.example.iptvsmarttvplayer.model.Channel


object M3uParser {

    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()

        var currentName: String? = null
        var currentLogo: String? = null
        var currentGroup: String? = null

        for (rawLine in lines) {
            val line = rawLine.trim()

            when {
                line.isEmpty() -> {
                    // skip blank lines
                }
                line.startsWith("#EXTM3U") -> {
                    // playlist header, nothing to extract
                }
                line.startsWith("#EXTINF") -> {
                    currentName = extractAttribute(line, "tvg-name")
                        ?: line.substringAfterLast(",").trim().ifBlank { null }
                    currentLogo = extractAttribute(line, "tvg-logo")
                    currentGroup = extractAttribute(line, "group-title")
                }
                line.startsWith("#") -> {
                    // other directives (e.g. #EXTVLCOPT, #EXTGRP) are ignored for now
                }
                else -> {
                    // Any non-comment, non-empty line is treated as the stream URL
                    val name = currentName ?: line
                    channels.add(
                        Channel(
                            name = name,
                            url = line,
                            logoUrl = currentLogo,
                            group = currentGroup
                        )
                    )
                    currentName = null
                    currentLogo = null
                    currentGroup = null
                }
            }
        }

        return channels
    }

    private fun extractAttribute(line: String, key: String): String? {
        val regex = Regex("$key=\"([^\"]*)\"")
        return regex.find(line)?.groupValues?.get(1)?.takeIf { it.isNotBlank() }
    }
}