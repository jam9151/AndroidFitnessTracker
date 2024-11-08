package com.example.androidfitnesstracker.Pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun StatsPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "This is the Stats Page")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigateUp() }) {
            Text("Back to Main Page")
        }
    }
}
