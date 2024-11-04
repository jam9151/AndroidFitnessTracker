package com.example.androidfitnesstracker

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage


/* //Sample workout page
@Composable
fun WorkoutPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "This is the Workout Page")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigateUp() }) {
            Text("Back to Main Page")
        }
    }
}*/



@Composable
fun WorkoutDetailScreen(workoutId: Int) {
    val dbHelper = UserDatabaseHelper.getInstance(LocalContext.current)
    val workout = dbHelper.getAllWorkouts().firstOrNull { it.id == workoutId }
    val steps = dbHelper.getExerciseSteps(workoutId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Text(
            text = workout?.name ?: "Workout",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        workout?.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(steps) { step ->
                ExerciseStepItem(step)
            }
        }
    }
}

@Composable
fun WorkoutItem(
    workout: Workout,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Material 3 syntax
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cover Image
            AsyncImage(
                model = workout.coverImage,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Workout Name and Description
            Column {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleLarge, // Material 3 syntax
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = workout.description,
                    style = MaterialTheme.typography.bodyLarge, // Material 3 syntax
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun WorkoutListScreen(
    workouts: List<Workout>,
    onWorkoutClick: (Workout) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(workouts) { workout ->
            WorkoutItem(
                workout = workout,
                onClick = { onWorkoutClick(workout) }
            )
        }
    }
}


@Composable
fun ExerciseStepItem(step: ExerciseStep) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Display step number and description
            Text(
                text = "Step ${step.stepNumber}: ${step.description}",
                style = MaterialTheme.typography.bodyLarge
            )

            // Display image if available
            step.image?.let { imageUrl ->
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}