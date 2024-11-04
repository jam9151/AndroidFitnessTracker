package com.example.androidfitnesstracker

data class DefaultExercise(
    val name: String,
    val description: String,
    val coverImage: String,
    val calories: Int,
    val duration: Int,
    val distance: Float,
    val steps: List<ExerciseStepData> = emptyList()
)

data class ExerciseStepData(
    val stepNumber: Int,
    val description: String,
    val image: String? = null
)

val defaultExercises = listOf(
    DefaultExercise(
        name = "Running",
        description = "Outdoor running on pavement, ideal for cardio and endurance.",
        coverImage = "path/to/running_image.jpg",
        calories = 400,
        duration = 30,
        distance = 3.0f
    ),
    DefaultExercise(
        name = "Walking",
        description = "A brisk walk on a flat surface, perfect for light exercise and stress relief.",
        coverImage = "path/to/walking_image.jpg",
        calories = 150,
        duration = 30,
        distance = 1.5f
    ),
    DefaultExercise(
        name = "Cycling",
        description = "A moderate cycling session on flat terrain.",
        coverImage = "path/to/cycling_image.jpg",
        calories = 300,
        duration = 45,
        distance = 10.0f
    ),
    DefaultExercise(
        name = "Yoga",
        description = "A basic yoga routine focusing on flexibility and relaxation.",
        coverImage = "path/to/yoga_image.jpg",
        calories = 200,
        duration = 40,
        distance = 0f,
        steps = listOf(
            ExerciseStepData(1, "Mountain Pose - Hold for 1 minute. A grounding posture to start the session.", "path/to/mountain_pose.jpg"),
            ExerciseStepData(2, "Downward Dog - Hold for 1 minute. Stretches the hamstrings and calves.", "path/to/downward_dog.jpg"),
            ExerciseStepData(3, "Warrior Pose - Hold for 2 minutes. Builds strength in legs and improves balance.", "path/to/warrior_pose.jpg"),
            ExerciseStepData(4, "Child's Pose - Hold for 1 minute. A resting pose for relaxation.", "path/to/child_pose.jpg"),
            ExerciseStepData(5, "Corpse Pose - Hold for 3 minutes. Focuses on deep relaxation and breathing.", "path/to/corpse_pose.jpg")
        )
    ),
    DefaultExercise(
        name = "High-Intensity Interval Training (HIIT)",
        description = "An intense HIIT workout for maximum calorie burn and endurance.",
        coverImage = "path/to/hiit_image.jpg",
        calories = 500,
        duration = 20,
        distance = 0f,
        steps = listOf(
            ExerciseStepData(1, "Jumping Jacks - 1 minute", "path/to/jumping_jacks.jpg"),
            ExerciseStepData(2, "Push-Ups - 1 minute", "path/to/push_ups.jpg"),
            ExerciseStepData(3, "High Knees - 1 minute", "path/to/high_knees.jpg"),
            ExerciseStepData(4, "Burpees - 1 minute", "path/to/burpees.jpg"),
            ExerciseStepData(5, "Mountain Climbers - 1 minute", "path/to/mountain_climbers.jpg"),
            ExerciseStepData(6, "Squat Jumps - 1 minute", "path/to/squat_jumps.jpg"),
            ExerciseStepData(7, "Rest - 1 minute", null)
        )
    ),
    DefaultExercise(
        name = "Weightlifting",
        description = "A general weightlifting routine focusing on arms and abs.",
        coverImage = "path/to/weightlifting_image.jpg",
        calories = 350,
        duration = 45,
        distance = 0f,
        steps = listOf(
            ExerciseStepData(1, "Bicep Curls - 3 sets of 12 reps", "path/to/bicep_curls.jpg"),
            ExerciseStepData(2, "Tricep Extensions - 3 sets of 12 reps", "path/to/tricep_extensions.jpg"),
            ExerciseStepData(3, "Bench Press - 3 sets of 10 reps", "path/to/bench_press.jpg"),
            ExerciseStepData(4, "Crunches - 3 sets of 15 reps", "path/to/crunches.jpg"),
            ExerciseStepData(5, "Plank - Hold for 1 minute", "path/to/plank.jpg")
        )
    )
)
