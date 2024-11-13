package com.example.androidfitnesstracker.Workout

import android.util.Log
import com.example.androidfitnesstracker.R

data class DefaultExercise(
    val name: String,
    val description: String,
    val imageResId: Int? = null,
    val calories: Int,
    val duration: Int,
    val distance: Float,
    val steps: Int,
    val instructions: List<ExerciseStepData> = emptyList(),
)

data class ExerciseStepData(
    val stepNumber: Int,
    val description: String,
    val image: Int? = null
)

val defaultExercises = listOf(

    DefaultExercise(
        name = "Running",
        description = "Outdoor running on pavement, ideal for cardio and endurance.",
        imageResId = R.drawable.running_small,
        calories = 400,
        duration = 30,
        distance = 3.0f,
        steps = 4500,
        instructions = listOf(ExerciseStepData(stepNumber = 1, description = "Go fast to keep a steady fast heart rate", image = R.drawable.running_man))
    ).also {
        Log.d("Workout Retrieval", "Workout: ${it.name}, Image ID: ${it.imageResId}")
    },
    DefaultExercise(
        name = "Walking",
        description = "A brisk walk on a flat surface, perfect for light exercise and stress relief.",
        imageResId = R.drawable.walking_small,
        calories = 150,
        duration = 30,
        distance = 1.5f,
        steps = 3000
    ),
    DefaultExercise(
        name = "Cycling",
        description = "A moderate cycling session on flat terrain.",
        imageResId = R.drawable.cycling_small,
        calories = 300,
        duration = 45,
        distance = 10.0f,
        steps = 3000
    ),
    DefaultExercise(
        name = "Yoga",
        description = "A basic yoga routine focusing on flexibility and relaxation.",
        imageResId = R.drawable.yoga_small,
        calories = 200,
        duration = 40,
        distance = 0f,
        steps = 0,
        instructions = listOf(
            ExerciseStepData(
                1,
                "Mountain Pose - Hold for 1 minute. A grounding posture to start the session.",
                null
            ),
            ExerciseStepData(
                2,
                "Downward Dog - Hold for 1 minute. Stretches the hamstrings and calves.",
                null
            ),
            ExerciseStepData(
                3,
                "Warrior Pose - Hold for 2 minutes. Builds strength in legs and improves balance.",
                null
            ),
            ExerciseStepData(
                4,
                "Child's Pose - Hold for 1 minute. A resting pose for relaxation.",
                null
            ),
            ExerciseStepData(
                5,
                "Corpse Pose - Hold for 3 minutes. Focuses on deep relaxation and breathing.",
                null
            )
        )
    ),
    DefaultExercise(
        name = "High-Intensity Interval Training (HIIT)",
        description = "An intense HIIT workout for maximum calorie burn and endurance.",
        imageResId = R.drawable.hiit_small,
        calories = 500,
        duration = 20,
        distance = 0f,
        steps = 1000,
        instructions = listOf(
            ExerciseStepData(1, "Jumping Jacks - 1 minute", null),
            ExerciseStepData(2, "Push-Ups - 1 minute", null),
            ExerciseStepData(3, "High Knees - 1 minute", null),
            ExerciseStepData(4, "Burpees - 1 minute", null),
            ExerciseStepData(5, "Mountain Climbers - 1 minute", null),
            ExerciseStepData(6, "Squat Jumps - 1 minute", null),
            ExerciseStepData(7, "Rest - 1 minute", null)
        )
    ),
    DefaultExercise(
        name = "Weightlifting",
        description = "A general weightlifting routine focusing on arms and abs.",
        imageResId = R.drawable.weightlifting_small,
        calories = 350,
        duration = 45,
        distance = 0f,
        steps = 0,
        instructions = listOf(
            ExerciseStepData(1, "Bicep Curls - 3 sets of 12 reps", null),
            ExerciseStepData(2, "Tricep Extensions - 3 sets of 12 reps", null),
            ExerciseStepData(3, "Bench Press - 3 sets of 10 reps", null),
            ExerciseStepData(4, "Crunches - 3 sets of 15 reps", null),
            ExerciseStepData(5, "Plank - Hold for 1 minute", null)
        )
    ),

)
