package com.example.practice.practice

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.practice.R
import com.example.practice.dowload.VideoDownloadViewModel
import com.example.practice.model.DownloadState
import com.example.practice.model.DownloadTaskUi
import com.example.practice.utils.TaskStatus


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

                /*Image(
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
                )*/
                Image(
                    painter = painterResource(id = R.drawable.box_image),
                    contentDescription = "Download Video",
                    modifier = Modifier
                        .size(90.dp)
                        .clickable {
                            val link = context.getClipboardUrl()
                            if (link.isNullOrBlank()) {
                                context.showToastMessage("No Url found")
                            } else {
                                viewModel.enqueueDownload(context, link)
                            }
                        }
                )

            }
            Spacer(modifier = Modifier.height(12.dp))
            // ---------------- PROGRESS LIST ----------------
            if (screenState.tasks.isEmpty()) {
                Text(
                    text = "Tap the box to download video",
                    modifier = Modifier.padding(top = 12.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(screenState.tasks) { task ->
                        DownloadProgress(task,viewModel)
                    }
                }
            }
        }
        /*when (screenState.downloadVideoState) {
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
                *//*DownloadProgress((screenState.downloadVideoState as DownloadState.Progress).percent)*//*
                    DownloadProgress()
                }
                DownloadState.Started -> {
                    Text(text = "Download Started Please wait...")
                }
            }*/
        Button(onClick = { navController.navigate("videoList") }) {
            Text("View Downloaded Videos")
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


}


@Composable
fun DownloadProgress(/*progress: Int*/task: DownloadTaskUi,viewModel: VideoDownloadViewModel) {
    /*Column(
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
    }*/
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        // Progress + % Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when (task.status) {
                    TaskStatus.PENDING -> "Pending"
                    TaskStatus.DOWNLOADING -> "Downloading ${task.progress}%"
                    TaskStatus.COMPLETED -> "Completed"
                    TaskStatus.FAILED -> "Failed"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            LinearProgressIndicator(
                progress = task.progress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Cancel button
        Icon(
           painter = painterResource(R.drawable.ic_cancel),
            contentDescription = "Cancel",
            modifier = Modifier
                .size(24.dp)
                .clickable { viewModel.cancelTask(task.id) },
            tint = Color.Red
        )
    }


}


@Preview
@Composable
fun PreviewHomeScreen() {
    HomesScreen(viewModel(), navController = rememberNavController())
}