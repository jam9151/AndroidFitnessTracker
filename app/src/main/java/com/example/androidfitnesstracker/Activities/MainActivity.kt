package com.example.androidfitnesstracker.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.androidfitnesstracker.Pages.DietPlanPage
import com.example.androidfitnesstracker.Pages.MainPage
import com.example.androidfitnesstracker.Pages.MealCreationPage
import com.example.androidfitnesstracker.Pages.MealDetailScreen
import com.example.androidfitnesstracker.Pages.MealListScreen
import com.example.androidfitnesstracker.Pages.MySubscriptionPage
import com.example.androidfitnesstracker.Pages.StatsPage
import com.example.androidfitnesstracker.Pages.UpgradePage
import com.example.androidfitnesstracker.Pages.WorkoutCreationPage
import com.example.androidfitnesstracker.Pages.*
import com.example.androidfitnesstracker.User.UserActivityManager
import com.example.androidfitnesstracker.User.UserDatabaseHelper
import com.example.androidfitnesstracker.User.UserSessionManager
import com.example.androidfitnesstracker.Pages.WorkoutDetailScreen
import com.example.androidfitnesstracker.Pages.WorkoutListScreen
import com.example.androidfitnesstracker.Workout.DietManager
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
        dbHelper.initializeBasicMeals() // Initialize default exercises

        enableEdgeToEdge()
        setContent {
            AndroidFitnessTrackerTheme {
                val navController = rememberNavController()
                val dietManager = DietManager(this@MainActivity)  // Initialize dietManager


                // State to store diet plans
                var diets by remember { mutableStateOf(dietManager.getDietPlans()) }


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

                    composable("statsPage") { StatsPage(
                        dbHelper = dbHelper,
                        sessionManager = sessionManager
                        )
                    }

                    composable("workoutList") {
                        WorkoutListScreen(
                            workouts = dbHelper.getAllWorkouts(),
                            onWorkoutClick = { workout ->
                                navController.navigate("workoutDetail/${workout.id}")
                            },
                            onAddWorkoutClick = {navController.navigate(("workoutCreationPage"))}
                        )
                    }

                    composable("mealPlan") {
                        MealListScreen(
                            meals = dbHelper.getAllMeals(),
                            onMealClick = {meal ->
                                navController.navigate("mealDetail/${meal.id}")
                            },
                            onAddMealClick = { navController.navigate(("mealCreationPage"))})
                    }
                    composable(
                        route = "workoutDetail/{workoutId}",
                        arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: return@composable
                        WorkoutDetailScreen(workoutId = workoutId,userActivityManager = userActivityManager)
                    }

                    composable("mySubscription") {
                        MySubscriptionPage(
                            dbHelper = dbHelper,
                            sessionManager = sessionManager,
                            onUpgradeClick = { navController.navigate("upgradePage") }
                        )
                    }
                    composable(
                        route = "mealDetail/{mealId}",
                        arguments = listOf(navArgument("mealId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val mealId = backStackEntry.arguments?.getInt("mealId") ?: return@composable
                        MealDetailScreen(mealId = mealId,userActivityManager = userActivityManager)
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

                    composable("workoutCreationPage") {
                        WorkoutCreationPage(
                            dbHelper = dbHelper,
                            onSave = {navController.navigate("workoutList")}
                        )
                    }

                    composable("mealCreationPage") {
                        MealCreationPage(
                            dbHelper = dbHelper,
                            onSave = {navController.navigate("mealPlan")}
                        )
                    }
                    composable("DietPlan") {
                        DietPlanPage(
                            navController = navController,
                            diets = dietManager.getDietPlans(),
                            onDietClick = { diet ->
                                navController.navigate("dietDetail/${diet.id}")
                            },
                            onDeleteCustomDiet = {   dietId    ->
                                dietManager.deleteCustomDiet(dietId)
                                navController.navigate("DietPlan") { launchSingleTop = true }
                            }
                        )
                    }






                    composable("createCustomDiet") {
                        CreateCustomDietPage(
                            navController = navController,
                            onCreateCustomDiet = { age, targetWeight, workoutType, targetCalories, ingredients ->
                                // Create custom diet and get its new ID
                                val newDietId = dietManager.createCustomDiet(age, targetWeight, workoutType, targetCalories, ingredients)
                                diets = dietManager.getDietPlans() // Refresh the diet list


                                // Navigate to the DietPlanPage of the specific custom diet
                                navController.navigate("dietDetail/$newDietId") { launchSingleTop = true }
                            }
                        )
                    }
                    composable("dietDetail/{dietId}") { backStackEntry ->
                        val dietId = backStackEntry.arguments?.getString("dietId")?.toIntOrNull() ?: 0
                        DietDetailPage(dietId = dietId, navController = navController)
                    }
                }
            }
        }
    }
}






