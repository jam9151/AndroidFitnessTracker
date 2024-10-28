package com.example.androidfitnesstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.androidfitnesstracker.ui.theme.AndroidFitnessTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AndroidFitnessTrackerTheme {
                val navController = rememberNavController()

                // Set up the NavHost with routes
                NavHost(navController = navController, startDestination = "mainPage") {
                    composable("mainPage") { MainPage(navController) }
                    composable("workoutPage") { WorkoutPage(navController) }
                    composable("statsPage") { StatsPage(navController) }
                }
            }
        }
    }
}






