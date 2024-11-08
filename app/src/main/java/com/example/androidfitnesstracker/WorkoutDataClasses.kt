package com.example.androidfitnesstracker


data class Workout(
    val id: Int,
    val name: String,
    val description: String,
    val coverImage: Int? = null, // File path or URI to cover image
    val calories: Int,
    val duration: Int, // in minutes
    val distance: Float, // in miles or kilometers
    val isCustom: Boolean,
    val steps: Int,
    val instructions: List<ExerciseStep> = emptyList() // Steps for each workout
)

data class ExerciseStep(
    val stepNumber: Int,
    val description: String,
    val image: Int? // File path or URI to step image
)

