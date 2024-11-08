package com.example.androidfitnesstracker.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.androidfitnesstracker.Pages.LeaderboardPage
import com.example.androidfitnesstracker.Pages.MainPage
import com.example.androidfitnesstracker.Pages.MealPlanPage
import com.example.androidfitnesstracker.Pages.MySubscriptionPage
import com.example.androidfitnesstracker.Pages.StatsPage
import com.example.androidfitnesstracker.Pages.UpgradePage
import com.example.androidfitnesstracker.User.UserActivityManager
import com.example.androidfitnesstracker.User.UserDatabaseHelper
import com.example.androidfitnesstracker.User.UserSessionManager
import com.example.androidfitnesstracker.Pages.WorkoutDetailScreen
import com.example.androidfitnesstracker.Pages.WorkoutListScreen
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
                    composable("statsPage") { StatsPage(navController) }
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

                    composable("mySubscription") {
                        MySubscriptionPage(
                            dbHelper = dbHelper,
                            sessionManager = sessionManager,
                            onUpgradeClick = { navController.navigate("upgradePage") }
                        )
                    }


                    composable(
                        route = "workoutDetail/{workoutId}",
                        arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: return@composable
                        WorkoutDetailScreen(workoutId = workoutId,userActivityManager = userActivityManager)
                    }

                    composable("upgradePage") {
                        UpgradePage(
                            dbHelper = dbHelper,
                            userId = sessionManager.getUserId()!!,
                            onUpgradeSuccess = {
                                navController.navigate("mySubscription")
                            }
                        )
                    }
                }
            }
        }
    }
}






