package com.example.practice.dowload

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.api.RetrofitInstance
import com.example.practice.model.DownloadState
import com.example.practice.model.DownloadedVideo
import com.example.practice.model.HomeScreenUiState
import com.example.practice.practice.getHighestResolutionFormat
import com.example.practice.practice.getInternalVideos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    private val _screenState = MutableStateFlow(HomeScreenUiState())
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
    }
}
