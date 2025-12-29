package com.example.practice.model

data class DownloadResponse(
    val status: Boolean,
    val title: String?,
    val medias: List<Media>?
)
