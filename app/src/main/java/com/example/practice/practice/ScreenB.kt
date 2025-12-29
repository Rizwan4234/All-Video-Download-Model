package com.example.practice.practice

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.practice.model.DownloadedVideo

@Composable
fun ScreenB(
    navController: NavController
) {
    VideoListScreen(viewModel(),navController)
}