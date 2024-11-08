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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.androidfitnesstracker.ui.theme.AndroidFitnessTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create an instance of UserActivityManager and get the current user ID
        val sessionManager = UserSessionManager(this)
        val userActivityManager = UserActivityManager(
            UserDatabaseHelper.getInstance(this),
            sessionManager)
        val dbHelper = UserDatabaseHelper.getInstance(this)
        dbHelper.initializeBasicExercises() // Initialize default exercises

        enableEdgeToEdge()
        setContent {
            AndroidFitnessTrackerTheme {
                val navController = rememberNavController()

                // Set up the NavHost with routes
                NavHost(navController = navController, startDestination = "mainPage") {
                    composable("mainPage") {
                        MainPage(
                            navController = navController,
                            userActivityManager = userActivityManager,
                            sessionManager = sessionManager,
                            dbHelper = dbHelper
                        )
                    }
                    //composable("workoutPage") { WorkoutPage(navController) }  //sample workout page
                    composable("statsPage") { StatsPage(navController) }
                    composable("mySubscription") { MySubscriptionPage(navController) }
                    composable("mealPlan") { MealPlanPage(navController) }
                    composable("leaderboardPage") { LeaderboardPage(navController) }

                    composable("workoutList") {
                        WorkoutListScreen(
                            workouts = dbHelper.getAllWorkouts(),
                            onWorkoutClick = { workout ->
                                navController.navigate("workoutDetail/${workout.id}")
                            }
                        )
                    }

                    composable(
                        route = "workoutDetail/{workoutId}",
                        arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: return@composable
                        WorkoutDetailScreen(workoutId = workoutId,userActivityManager = userActivityManager)
                    }
                }
            }
        }
    }
}






