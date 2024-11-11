package com.example.androidfitnesstracker.Pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.androidfitnesstracker.User.UserDatabaseHelper
import com.example.androidfitnesstracker.Workout.ExerciseStep
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.androidfitnesstracker.ui.theme.getImageFromInternalStorage
import com.example.androidfitnesstracker.ui.theme.saveImageToInternalStorage


@Composable
fun WorkoutCreationPage(
    onSave: () -> Unit,
    dbHelper: UserDatabaseHelper,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Workout details
    var workoutName by remember { mutableStateOf("New Workout Name") }
    var workoutDescription by remember { mutableStateOf("Your description here") }
    var calories by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var stepsRequired by remember { mutableStateOf("") }
    var coverImage by remember { mutableStateOf<Int?>(null) }

    // List of Exercise Steps
    val exerciseSteps = remember { mutableStateListOf<ExerciseStep>() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val resourceId = saveImageToInternalStorage(context, it)
            if (resourceId != null) {
                coverImage = resourceId // Assign the simulated ID
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(16.dp)
    ) {
       //TODO - test adding cover image
        val bitmap = getImageFromInternalStorage(context, coverImage ?: 0)
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Workout Image",
                modifier = Modifier.size(128.dp)
            )
        }
        // Editable Workout Details
        TextField(
            value = workoutName,
            onValueChange = { workoutName = it },
            label = { Text("Workout Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = workoutDescription,
            onValueChange = { workoutDescription = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Calories Burned") },
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (mins)") },
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                label = { Text("Distance") },
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = stepsRequired,
                onValueChange = { stepsRequired = it },
                label = { Text("Steps") },
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Cover Image")
        }

        // Dynamic List of Steps
        Text(
            text = "Steps",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        exerciseSteps.forEachIndexed { index, step ->

            //TODO - test this (test adding image inside a step)
            step.image?.let { imageUri ->
                Image(
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = "Step Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    TextField(
                        value = step.description,
                        onValueChange = {
                            exerciseSteps[index] =
                                step.copy(description = it) // Update step description
                        },
                        label = { Text("Step Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    val imagePickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        uri?.let {
                            val resourceId = saveImageToInternalStorage(context, it)
                            if (resourceId != null) {
                                exerciseSteps[index] = step.copy(image = resourceId) // Update the image for this step
                            }
                        }
                    }
                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*") // Launch the picker to select images
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Upload Step Image")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Add Step Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp) // Adjust size as needed
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)) // Background color for the button
                    .clickable {
                        exerciseSteps.add(ExerciseStep(stepNumber = exerciseSteps.size + 1, description = "", image = null))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.displayMedium.copy(color = Color.White) // Adjust typography and color
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                // Parse and save the workout
                val workoutId = dbHelper.insertWorkoutIfNotExists(
                    name = workoutName,
                    description = workoutDescription,
                    coverImage = coverImage ?: 0, // Default image placeholder
                    calories = calories.toIntOrNull() ?: 0,
                    duration = duration.toIntOrNull() ?: 0,
                    distance = distance.toFloatOrNull() ?: 0f,
                    steps = stepsRequired.toIntOrNull() ?: 0
                )
                workoutId?.let { id ->
                    exerciseSteps.forEach { step ->
                        dbHelper.insertExerciseStep(
                            workoutId = id,
                            stepNumber = step.stepNumber,
                            description = step.description,
                            image = step.image
                        )
                    }
                    onSave()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Workout")
        }
    }
}

