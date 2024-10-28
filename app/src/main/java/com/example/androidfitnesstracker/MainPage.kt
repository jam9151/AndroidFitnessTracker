package com.example.androidfitnesstracker

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome to Main Page")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("workoutPage") }) {
            Text("Go to Workout Page")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("statsPage") }) {
            Text("Go to Stats Page")
        }
    }
}
