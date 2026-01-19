package com.example.practice

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practice.practice.HomesScreen
import com.example.practice.practice.ScreenB
import com.example.practice.practice.VideoListScreen
import com.example.practice.practice.VideoPlayerItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "screenA"
            ) {
                composable("screenA") { HomesScreen(viewModel(), navController) }
                composable("screenB") { ScreenB(navController) }


                composable("videoList") { VideoListScreen(viewModel(),navController)}

            }
        }
    }
}

