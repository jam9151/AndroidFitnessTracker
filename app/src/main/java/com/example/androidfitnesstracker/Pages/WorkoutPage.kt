package com.example.androidfitnesstracker.Pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.androidfitnesstracker.R
import com.example.androidfitnesstracker.User.UserActivityManager
import com.example.androidfitnesstracker.User.UserDatabaseHelper
import com.example.androidfitnesstracker.Workout.ExerciseStep
import com.example.androidfitnesstracker.Workout.Workout
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


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
fun WorkoutDetailScreen(workoutId: Int, userActivityManager: UserActivityManager) {
    val dbHelper = UserDatabaseHelper.getInstance(LocalContext.current)
    val workout = dbHelper.getAllWorkouts().firstOrNull { it.id == workoutId }
    val steps = dbHelper.getExerciseSteps(workoutId)

    // Timer state
    var timerRunning by remember { mutableStateOf(false) }
    var timeElapsed by remember { mutableStateOf(0L) } // Time in milliseconds

    // Update timer every 10 milliseconds when running
    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timerRunning) {
                kotlinx.coroutines.delay(10L)
                timeElapsed += 10
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        // Timer section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Calculate hours, minutes, seconds, and milliseconds
            val hours = timeElapsed / 3_600_000
            val minutes = (timeElapsed % 3_600_000) / 60_000
            val seconds = (timeElapsed % 60_000) / 1_000
            val milliseconds = (timeElapsed % 1_000) / 10

            Text(
                text = String.format(
                    "Time: %02d:%02d:%02d.%02d",
                    hours, minutes, seconds, milliseconds
                ),
                style = MaterialTheme.typography.displayMedium, // Larger text style

            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { timerRunning = true },
                    enabled = !timerRunning
                ) {
                    Text("Start")
                }
                Button(
                    onClick = { timerRunning = false },
                    enabled = timerRunning
                ) {
                    Text("Stop")
                }
                Button(
                    onClick = {
                        timerRunning = false
                        timeElapsed = 0L
                    }
                ) {
                    Text("Reset")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Workout details
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

        Spacer(modifier = Modifier.weight(1f)) // Pushes button to the bottom
        workout?.let {
            Button(
                onClick = {
                    timerRunning = false // Stop timer on completion
                    userActivityManager.logWorkoutCompletion(it)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Complete Workout")
            }
        }
    }
}


@Composable
fun WorkoutItem(
    workout: Workout,
    onClick: () -> Unit
) {
    //Log.d("WorkoutItem", "Displaying workout: ${workout.name}, coverImageResId: ${workout.coverImage}")
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
            // Check coverImageResId, assign to placeholder if invalid
            val imageResId = workout.coverImage?.takeIf { it != 0 } ?: R.drawable.placeholder

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = workout.name,
                modifier = Modifier.size(100.dp)
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
    onWorkoutClick: (Workout) -> Unit,
    onAddWorkoutClick: () -> Unit
) {
    // State to control the rotation angle
    val rotationAngle = remember { mutableFloatStateOf(0f) }

    // State to track if the wheel is spinning
    var isSpinning by remember { mutableStateOf(false) }

    // Coroutine scope for animation
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Box to overlay the arrow and spinning wheel
        Box(
            modifier = Modifier.size(200.dp), // Match the size of the spinning wheel
            contentAlignment = Alignment.Center
        ) {
            // Stationary Arrow
            Image(
                painter = painterResource(id = R.drawable.down_arrow), // Replace with your arrow resource
                contentDescription = "Arrow",
                modifier = Modifier
                    .size(50.dp) // Adjust the size of the arrow to fit above the wheel
                    .align(Alignment.TopCenter) // Ensure arrow stays at the top
            )

            // Spinning Wheel with Padding
            Image(
                painter = painterResource(id = R.drawable.spin_wheel_2),
                contentDescription = "Spinning Wheel",
                modifier = Modifier
                    .size(200.dp)
                    .padding(top = 40.dp) // Add padding between the arrow and the wheel
                    .graphicsLayer(rotationZ = rotationAngle.value) // Apply rotation
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Let the Wheel Decide Button
        Button(
            onClick = {
                if (!isSpinning) {
                    isSpinning = true
                    coroutineScope.launch {
                        spinWheelAndNavigate(workouts, onWorkoutClick, rotationAngle) {
                            isSpinning = false // Reset spinning state after navigation
                        }
                    }
                }
            }
        ) {
            Text("LET THE WHEEL DECIDE YOUR FATE")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // LazyColumn for the Workout List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(workouts, key = { it.id }) { workout ->
                WorkoutItem(
                    workout = workout,
                    onClick = { onWorkoutClick(workout) }
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp) // Add spacing above and below the button
                        .align(Alignment.CenterHorizontally), // Horizontally center the button
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp) // Set the size of the circle
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)) // Green background
                            .clickable { onAddWorkoutClick() }, // Click handler for navigation
                        contentAlignment = Alignment.Center // Center the "+" symbol
                    ) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.displayMedium, // Adjust text size
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}





suspend fun spinWheelAndNavigate(
    workouts: List<Workout>,
    onWorkoutSelected: (Workout) -> Unit,
    rotationAngle: MutableState<Float>, // Use MutableState<Float> here
    onFinish: () -> Unit
) {
    // Simulate spinning animation
    val randomAngle = (360 * 3) + (0..360).random() // At least 3 full rotations
    val duration = 7000L // Total duration in milliseconds
    val startTime = System.currentTimeMillis()

    while (System.currentTimeMillis() - startTime < duration) {
        val elapsedTime = System.currentTimeMillis() - startTime
        val progress = elapsedTime / duration.toFloat()
        val easedProgress = (1 - (1 - progress) * (1 - progress)) // Ease-out effect
        rotationAngle.value = randomAngle * easedProgress
        delay(16) // 16ms for smooth animation (~60fps)
    }

    // Finalize rotation
    rotationAngle.value %= 360 // Normalize the angle

    // Select a random workout
    val randomWorkout = workouts.random()

    // Navigate to the selected workout
    onWorkoutSelected(randomWorkout)

    // Call onFinish callback
    onFinish()
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