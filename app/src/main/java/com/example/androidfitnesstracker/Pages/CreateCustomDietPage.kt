//CreateCustomDietPage.kt
package com.example.androidfitnesstracker.Pages
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun CreateCustomDietPage(
    navController: NavController,
    onCreateCustomDiet: (Int, Int, String, Int, List<String>) -> Unit
) {
    var age by remember { mutableStateOf("") }
    var targetWeight by remember { mutableStateOf("") }
    var workoutType by remember { mutableStateOf("") }
    var targetCalories by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Create Custom Diet", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))


        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = targetWeight,
            onValueChange = { targetWeight = it },
            label = { Text("Target Weight") },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = workoutType,
            onValueChange = { workoutType = it },
            label = { Text("Workout Type") },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = targetCalories,
            onValueChange = { targetCalories = it },
            label = { Text("Target Calories") },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = ingredients,
            onValueChange = { ingredients = it },
            label = { Text("Ingredients (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {
                onCreateCustomDiet(
                    age.toIntOrNull() ?: 0,
                    targetWeight.toIntOrNull() ?: 0,
                    workoutType,
                    targetCalories.toIntOrNull() ?: 0,
                    ingredients.split(",").map { it.trim() }
                )
                navController.navigate("mealPlan") {
                    launchSingleTop = true  // Ensures only one instance is in the stack
                }
                navController.popBackStack()  // Navigate back after creation
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Diet")
        }


        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go Back")
        }
    }
}
