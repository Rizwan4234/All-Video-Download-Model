package com.example.practice.model

import com.example.practice.utils.TaskStatus
import kotlinx.coroutines.Job

data class DownloadTaskUi(
    val id: String,
    val originalUrl: String,
    val progress: Int = 0,
    val status: TaskStatus = TaskStatus.PENDING,
    var job: Job? = null // <- Add this for cancel

)
