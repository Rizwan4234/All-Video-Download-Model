package com.example.practice.model

import java.io.File

sealed class DownloadState {
    object Idle : DownloadState()
    object Started : DownloadState()
    data class Progress(val percent: Int) : DownloadState()
    data class Completed(val file: File) : DownloadState()
    data class Failure(val error: String) : DownloadState()
}