package com.example.practice.model

data class VideoUiState (
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val title: String = "",
    val thumbnail: String = "",
    val downloadedVideos: List<String> = emptyList(),
    val error: String? = null
    )