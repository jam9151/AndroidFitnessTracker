package com.example.androidfitnesstracker.Pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidfitnesstracker.Workout.DietPlan
import com.example.androidfitnesstracker.ui.theme.GoBackButton

@Composable
fun DietPlanPage(
    navController: NavController,
    diets: List<DietPlan>,
    onDietClick: (DietPlan) -> Unit,
    onDeleteCustomDiet: (Int) -> Unit

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page Title
        Text("Diet Plans", fontSize = 24.sp, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // List of Diets
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(diets) { diet ->

                DietPlanCard(
                    diet = diet,
                    onClick = { onDietClick(diet) },
                    onDelete = if (diet.isCustom) { { onDeleteCustomDiet(diet.id) } } else null
                )
            }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("createCustomDiet") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Custom Diet")
            }
        }
    }

        // Go Back Button
        GoBackButton(navController)
    }
}

@Composable
fun DietPlanCard(
    diet: DietPlan,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = diet.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Calories: ${diet.calories} kcal", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = diet.description, style = MaterialTheme.typography.bodyMedium)

            // Show the delete button only for custom diets
            onDelete?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = it) {
                    Text("Delete Custom Diet")
                }
            }
        }
    }
}
