package com.example.practice.api

data class VideoResponse(
    val title: String,
    val thumbnail: String,
    val formats: List<VideoFormat>
)
