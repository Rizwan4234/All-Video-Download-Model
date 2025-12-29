package com.example.practice.model

import androidx.compose.runtime.mutableStateListOf
import com.example.practice.api.VideoApi

/*
class VideoRepository(private val api: VideoApi) {
    suspend fun getVideo(videoUrl: String): SimpleVideo {
        val response = api.getVideoInfo(videoUrl)
        val videoFormat = response.formats.firstOrNull { it.vcodec != "none" && it.url != null }
        return SimpleVideo(
            title = response.title,
            thumbnail = response.thumbnail,
            videoUrl = videoFormat?.url
        )
    }
}*/
