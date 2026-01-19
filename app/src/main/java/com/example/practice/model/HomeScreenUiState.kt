package com.example.practice.model

data class HomeScreenUiState(
    val downloadVideoState: DownloadState = DownloadState.Idle,
    val currentVideosList: List<DownloadedVideo> = emptyList(),
    val tasks: List<DownloadTaskUi> = emptyList(),
    val currentDownloadingUrl: String? = null

)
