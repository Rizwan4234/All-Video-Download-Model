package com.example.practice.practice

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.practice.R
import com.example.practice.dowload.VideoDownloadViewModel
import com.example.practice.model.DownloadState


@Composable
fun HomesScreen(
    viewModel: VideoDownloadViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val screenState by viewModel.screenState.collectAsState()
    val showLottie =
        screenState.downloadVideoState is DownloadState.Started ||
                screenState.downloadVideoState is DownloadState.Progress

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                if (showLottie) {
                    DownloadLottie()
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.box_image),
                        contentDescription = "Download Video",
                        modifier = Modifier
                            .size(90.dp)
                            //.align(Alignment.CenterHorizontally)
                            .clickable {
                                val link = context.getClipboardUrl()
                                if (link.isNullOrBlank()) {
                                    context.showToastMessage("No Url found")
                                } else {
                                    if (link.contains("tiktok.com")) {

                                        viewModel.startDownloadTiktok(
                                            context,
                                            "http://159.203.143.191/download?url=$link"
                                        )
                                    }
                                    else {
                                        viewModel.startDownload(context, link)
                                    }
                                }
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            when (screenState.downloadVideoState) {
                is DownloadState.Completed -> {
                    Text(text = "Download Completed")
                }
                is DownloadState.Failure -> {
                    Text(text = "Something went wrong while downloading video. Please try again!")
                }
                DownloadState.Idle -> {
                    Text(text = "Tap the box to download video")
                }
                is DownloadState.Progress -> {
                    DownloadProgress((screenState.downloadVideoState as DownloadState.Progress).percent)
                }
                DownloadState.Started -> {
                    Text(text = "Download Started Please wait...")
                }
            }
        }
        /*Button(onClick = {
        navController.navigate("screenB")
        val vidoes = context.getInternalVideos()
        Log.d("checkvideos","TotalVideos: $vidoes")
        vidoes.forEach { videoFile->
            Log.d("checkvideos","Video: $videoFile")
        }
    }) {
        Text(text = "Videos")
    }*/
        Button(onClick = { navController.navigate("videoList") }) {
            Text("View Downloaded Videos")
        }
    }
}


@Composable
fun DownloadProgress(progress: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Downloading: $progress%", modifier = Modifier.padding(bottom = 8.dp))
        LinearProgressIndicator(
            progress = progress / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}


@Preview
@Composable
fun PreviewHomeScreen() {
    HomesScreen(viewModel(), navController = rememberNavController())
}