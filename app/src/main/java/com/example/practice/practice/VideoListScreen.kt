package com.example.practice.practice

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.practice.Video.NoCheckVideos
import com.example.practice.dowload.VideoDownloadViewModel
import com.example.practice.model.DownloadedVideo
import java.io.File

@Composable
fun VideoListScreen(

    viewModel: VideoDownloadViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var videos by remember { mutableStateOf(context.getInternalVideos()) }

    var playingVideoPath by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Downloaded Videos", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
        if (videos.isEmpty()) {
            NoCheckVideos()
        }
        else {
            LazyColumn {
                items(videos) { video ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                playingVideoPath = video.path // set current video to play
                            },
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(video.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        IconButton(
                            onClick = {
                                // Delete file
                                val file = File(video.path)
                                if (file.exists()) file.delete()
                                // Refresh list
                                videos = context.getInternalVideos()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Video",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // If a video is selected, show ExoPlayer in-place
        playingVideoPath?.let { path ->
            VideoPlayerItem(videoPath = path) {
                // Back button pressed inside the player
                playingVideoPath = null
            }
        }
    }
}

@Composable
fun VideoPlayerItem(videoPath: String, onBack: () -> Unit) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.fromFile(File(videoPath))))
            prepare()
            playWhenReady = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full-screen player
        AndroidView(
            factory = { PlayerView(context).apply { player = exoPlayer; useController = true } },
            modifier = Modifier.fillMaxSize()
        )

        // Optional back button overlay
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart) // top-left corner
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
}

