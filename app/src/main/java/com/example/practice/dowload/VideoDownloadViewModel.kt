package com.example.practice.dowload

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.api.RetrofitInstance
import com.example.practice.model.DownloadState
import com.example.practice.model.DownloadTaskUi
import com.example.practice.model.DownloadedVideo
import com.example.practice.model.HomeScreenUiState
import com.example.practice.practice.getHighestResolutionFormat
import com.example.practice.practice.getInternalVideos
import com.example.practice.utils.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class VideoDownloadViewModel(application: Application) : AndroidViewModel(application) {
    /*private val _screenState = MutableStateFlow(HomeScreenUiState())
    val screenState = _screenState.asStateFlow()
    fun startDownload(context: Context, link: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.fetchVideoInfo(link)
                val format = response.getHighestResolutionFormat()
                format?.url?.let {
                    _screenState.update { it.copy(
                        downloadVideoState = DownloadState.Started
                    ) }

                    downloadToInternal(
                        context = context,
                        url = it,
                        title = response.title,
                        onComplete = {downloadedFile->
                            _screenState.update { it.copy(
                                downloadVideoState = DownloadState.Idle,//DownloadState.Completed(downloadedFile),
                                currentVideosList = context.getInternalVideos()
                                ) }
                        },
                        onProgress = {progress->
                            Log.d("currentProgress","Progress: $progress")
                            _screenState.update { it.copy(
                                downloadVideoState = DownloadState.Progress(progress)
                            ) }
                        },
                        onFailure = {errorMessage->
                            _screenState.update { it.copy(
                                downloadVideoState = DownloadState.Failure(errorMessage)
                            ) }
                            Log.d("checkprogress","Failure: $it")
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("VideoVM", "Download failed", e)
            }
        }
    }
    fun startDownloadTiktok(context: Context, url: String) {
        Log.d("checkurl","Url: $url")
        _screenState.update { it.copy(
            downloadVideoState = DownloadState.Started
        ) }
        downloadToInternal(
            context = context,
            url = url,
            title = System.currentTimeMillis().toString(),
            onComplete = {downloadedFile->
                _screenState.update { it.copy(
                    downloadVideoState = DownloadState.Completed(downloadedFile),
                    currentVideosList = context.getInternalVideos()
                ) }
                viewModelScope.launch {
                    delay(1500L)
                    _screenState.update { it.copy(
                        downloadVideoState = DownloadState.Idle,
                    ) }
                }
            },
            onProgress = {progress->
                Log.d("currentProgress","Progress: $progress")
                _screenState.update { it.copy(
                    downloadVideoState = DownloadState.Progress(progress)
                ) }
            },
            onFailure = {errorMessage->
                _screenState.update { it.copy(
                    downloadVideoState = DownloadState.Failure(errorMessage)
                ) }
            }
        )
    }
    private fun downloadToInternal(
        context: Context,
        url: String,
        title: String = System.currentTimeMillis().toString(),
        onComplete: (File) -> Unit = {},
        onProgress: (Int) -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileName = "$title.mp4"
                val dir = File(context.filesDir, "Videos")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, fileName)

                withContext(Dispatchers.Main) { onProgress(0) }

                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                val totalSize = connection.contentLengthLong
                val input = connection.inputStream
                val output = FileOutputStream(file)
                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int
                var downloadedSize = 0L

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    downloadedSize += bytesRead
                    val progress = if (totalSize > 0L) {
                        ((downloadedSize * 100 / totalSize).toInt()).coerceIn(0, 100)
                    } else 0

                    Log.d(
                        "checkCurrentProgress",
                        "Total Size: $totalSize DownloadSize: $downloadedSize Progress: $progress"
                    )

                    withContext(Dispatchers.Main){
                        onProgress(progress)
                    }
                }

                output.flush()
                output.close()
                input.close()
                connection.disconnect()

                withContext(Dispatchers.Main) {
                    onComplete(file)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure(e.message ?: "Unknown error")
                }
            }
        }
    }


    fun deleteVideo(context: Context, video: DownloadedVideo) {
        viewModelScope.launch {
            try {
                val file = File(video.path)
                if (file.exists()) {
                    file.delete()
                }
                val videos = context.getInternalVideos()
                _screenState.update { it.copy(
                    currentVideosList = videos
                ) }
            } catch (e: Exception) {
                Log.e("VideoVM", "Delete failed", e)
            }
        }
    }

    fun loadSavedVideos(context: Context){
        viewModelScope.launch {
            try {
                val videos = context.getInternalVideos()
                _screenState.update { it.copy(
                    currentVideosList = videos
                ) }
            } catch (e: Exception) {
                Log.e("VideoVM", "Delete failed", e)
            }
        }
    }*/

    // ---------------- UI STATE ----------------
    private val _screenState = MutableStateFlow(HomeScreenUiState())
    val screenState = _screenState.asStateFlow()

    private val downloadQueue = ArrayDeque<DownloadTaskUi>()
    private var isDownloading = false

    // -------------------- ENQUEUE DOWNLOAD --------------------
    fun enqueueDownload(context: Context, link: String) {
        val task = DownloadTaskUi(
            id = System.currentTimeMillis().toString(),
            originalUrl = link
        )

        downloadQueue.addLast(task)
        _screenState.update { it.copy(tasks = it.tasks + task) }

        if (!isDownloading) startNextDownload(context)
    }

    // -------------------- START NEXT --------------------
    private fun startNextDownload(context: Context) {
        val task = downloadQueue.removeFirstOrNull() ?: return
        isDownloading = true
        updateTask(task.id) { it.copy(status = TaskStatus.DOWNLOADING, progress = 0) }
        val job = viewModelScope.launch {
            val isTikTok = task.originalUrl.contains("tiktok.com", ignoreCase = true)
            try {
                if (isTikTok) startTikTokDownload(context, task)
                else startNormalDownload(context, task)
            } catch (e: Exception) {
                markFailed(task.id)
                finishAndMoveNext(context)
            }
        }
        updateTask(task.id) { it.copy(job = job) }
    }

    // -------------------- NORMAL --------------------
    private suspend fun startNormalDownload(context: Context, task: DownloadTaskUi) {
        val response = RetrofitInstance.api.fetchVideoInfo(task.originalUrl)
        val format = response.getHighestResolutionFormat() ?: throw Exception("No format")
        downloadFile(context, format.url, response.title, task.id)
    }

    // -------------------- TIKTOK --------------------
    private suspend fun startTikTokDownload(context: Context, task: DownloadTaskUi) {
    //    val finalUrl = "http://159.203.143.191/download?url=${task.originalUrl}"
        val finalUrl = "https://redhole.cofencode.com/download?url=${task.originalUrl}"
        downloadFile(context, finalUrl, System.currentTimeMillis().toString(), task.id)
    }

    // -------------------- DOWNLOAD FILE --------------------
    private suspend fun downloadFile(context: Context, url: String, title: String, taskId: String) = withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, "Videos/$title.mp4")
            file.parentFile?.mkdirs()
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.connect()

            val total = conn.contentLengthLong
            val input = conn.inputStream
            val output = FileOutputStream(file)

            val buffer = ByteArray(8 * 1024)
            var downloaded = 0L
            var read: Int

            while (input.read(buffer).also { read = it } != -1) {
                ensureActive()
                output.write(buffer, 0, read)
                downloaded += read
                val progress = ((downloaded * 100) / total).toInt().coerceIn(0, 100)
                withContext(Dispatchers.Main) { updateTaskProgress(taskId, progress) }
            }

            output.close()
            input.close()
            conn.disconnect()

            withContext(Dispatchers.Main) { finishTask(taskId, context) }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) { markFailed(taskId); finishAndMoveNext(context) }
        }
    }

    // -------------------- TASK HELPERS --------------------
    private fun updateTask(taskId: String, update: (DownloadTaskUi) -> DownloadTaskUi) {
        _screenState.update { state ->
            state.copy(tasks = state.tasks.map { if (it.id == taskId) update(it) else it })
        }
    }

    private fun updateTaskProgress(taskId: String, progress: Int) {
        updateTask(taskId) { it.copy(progress = progress) }
    }

    private fun markFailed(taskId: String) {
        _screenState.update { state ->
            state.copy(tasks = state.tasks.filter { it.id != taskId })
        }
    }

    fun cancelTask(taskId: String) {
        val task = _screenState.value.tasks.find { it.id == taskId } ?: return
        task.job?.cancel()
        downloadQueue.removeIf { it.id == taskId }

        _screenState.update { state ->
            state.copy(tasks = state.tasks.filter { it.id != taskId })
        }

        finishAndMoveNext(getApplication())
    }

    private fun finishTask(taskId: String, context: Context) {
        updateTask(taskId) { it.copy(progress = 100, status = TaskStatus.COMPLETED) }
        _screenState.update { it.copy(tasks = it.tasks.filter { it.id != taskId }) }
        finishAndMoveNext(context)
    }

    private fun finishAndMoveNext(context: Context) {
        isDownloading = false
        if (downloadQueue.isNotEmpty()) startNextDownload(context)
        _screenState.update { it.copy(currentVideosList = context.getInternalVideos()) }
    }

}
