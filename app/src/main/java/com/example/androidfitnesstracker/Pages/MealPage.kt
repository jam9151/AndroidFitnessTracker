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
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import com.example.androidfitnesstracker.Meal.Meal
import com.example.androidfitnesstracker.Meal.MealStep
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun MealDetailScreen(mealId: Int, userActivityManager: UserActivityManager) {
    val dbHelper = UserDatabaseHelper.getInstance(LocalContext.current)
    val meal = dbHelper.getAllMeals().firstOrNull { it.id == mealId }
    val steps = dbHelper.getMealSteps(mealId)

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

        // Meal details
        Text(
            text = meal?.name ?: "Meal",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        meal?.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(steps) { step ->
                MealStepItem(step)
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes button to the bottom
        meal?.let {
            Button(
                onClick = {
                    timerRunning = false // Stop timer on completion
                    userActivityManager.logMealCompletion(it)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Complete Meal")
            }
        }
    }
}

@Composable
fun MealItem(
    meal: Meal,
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
            // Check coverImageResId, assign to placeholder if invalid
            val imageResId = meal.coverImage?.takeIf { it != 0 } ?: R.drawable.placeholder

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = meal.name,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Meal Name and Description
            Column {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleLarge, // Material 3 syntax
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = meal.description,
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
fun MealListScreen(
    meals: List<Meal>,
    onMealClick: (Meal) -> Unit,
    onAddMealClick: () -> Unit
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
                        spinWheelAndNavigate(meals, onMealClick, rotationAngle) {
                            isSpinning = false // Reset spinning state after navigation
                        }
                    }
                }
            }
        ) {
            Text("LET THE WHEEL DECIDE YOUR FATE")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // LazyColumn for the Meal List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(meals, key = { it.id }) { meal ->
                MealItem(
                    meal = meal,
                    onClick = { onMealClick(meal) }
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
                            .clickable { onAddMealClick() }, // Click handler for navigation
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
    meals: List<Meal>,
    onMealSelected: (Meal) -> Unit,
    rotationAngle: MutableState<Float>, // Use MutableState<Float> here
    onFinish: () -> Unit
) {
    val randomAngle = (360 * 3) + (0..360).random()
    val duration = 7000L
    val startTime = System.currentTimeMillis()

    while (System.currentTimeMillis() - startTime < duration) {
        val elapsedTime = System.currentTimeMillis() - startTime
        val progress = elapsedTime / duration.toFloat()
        val easedProgress = (1 - (1 - progress) * (1 - progress))
        rotationAngle.value = randomAngle * easedProgress
        delay(16)
    }

    rotationAngle.value %= 360
    val randomMeal = meals.random()
    onMealSelected(randomMeal)
    onFinish()
}

@Composable
fun MealStepItem(step: MealStep) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Step ${step.stepNumber}: ${step.description}",
                style = MaterialTheme.typography.bodyLarge
            )

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
