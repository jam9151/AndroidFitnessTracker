//DietDetailPage.kt
package com.example.androidfitnesstracker.Pages


import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.androidfitnesstracker.Workout.DietPlan
import com.example.androidfitnesstracker.User.UserDatabaseHelper
import com.example.androidfitnesstracker.Workout.DietManager


@Composable
fun DietDetailPage(dietId: Int, navController: NavController) {
    val context = LocalContext.current
    var dietDetails by remember { mutableStateOf<DietPlan?>(null) }
    val dietManager = DietManager(context)


    // Fetch diet details when the page loads or when dietId changes
    LaunchedEffect(dietId) {
        dietDetails = dietManager.getDietById(dietId)
    }


    if (dietDetails == null) {
        // Display a message if diet details are not found
        Text("No diet found for this ID", style = MaterialTheme.typography.bodyLarge)
    } else {
        // Display the diet details
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Diet Name: ${dietDetails!!.name}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))


            Text("Calories: ${dietDetails!!.calories} kcal", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))


            Text("Description: ${dietDetails!!.description}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))


            Text("Ingredients:", style = MaterialTheme.typography.headlineMedium)
            dietDetails!!.ingredients.forEach { ingredient ->
                Text("- $ingredient", style = MaterialTheme.typography.bodyMedium)
            }


            Spacer(modifier = Modifier.height(16.dp))


            // Button to navigate back
            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun DietDetailPagePreview() {
    val navController = rememberNavController()
    DietDetailPage(dietId = 1, navController = navController)
}
