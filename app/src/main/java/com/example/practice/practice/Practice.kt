package com.example.practice.practice

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.practice.R
import com.example.practice.dowload.VideoDownloadViewModel
import java.io.File

@Composable
fun Testing(
    viewModel: VideoDownloadViewModel
) {

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val screenState by viewModel.screenState.collectAsState()
    var selectedVideo by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadSavedVideos(context)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.box_image),
            contentDescription = "Download Video",
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.CenterHorizontally)
                .clickable {
                    val link = clipboard.getText()?.text ?: return@clickable

                    if (link.contains("tiktok.com")) {
                        viewModel.startDownloadTiktok(
                            context,
                            "http://159.203.143.191/download?url=$link"
                        )
                    } else {
                        viewModel.startDownload(context, link)
                    }
                }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Downloaded Videos",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))
        if (screenState.currentVideosList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No videos downloaded yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(screenState.currentVideosList) { video ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                selectedVideo = video.path
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            IconButton(
                                onClick = {
                                    viewModel.deleteVideo(context, video)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Video",
                                    tint = Color.Red
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = video.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
        Button(onClick = {
            val vidoes = context.getInternalVideos()
            Log.d("checkvideos","TotalVideos: $vidoes")
            vidoes.forEach { videoFile->
                    Log.d("checkvideos","Video: $videoFile")
                }
        }) {
            Text(text = "Get Videos")
        }
    }
}

@Composable
fun VideoPlayerItemDu(videoPath: String, onBack: () -> Unit) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.fromFile(File(videoPath))))
            prepare()
            playWhenReady = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
fun DownloadLottie() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.down_load))
    val progress by animateLottieCompositionAsState(composition, isPlaying = true, iterations = LottieConstants.IterateForever)
    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier.fillMaxSize()
    )
}

fun saveVideoToGallery(context: Context, filePath: String) {
    val file = File(filePath)

    val values = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
    }

    val uri = context.contentResolver.insert(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        values
    )

    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { out ->
            file.inputStream().copyTo(out)
        }
    }
}
