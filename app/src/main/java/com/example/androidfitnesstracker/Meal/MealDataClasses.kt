package com.example.androidfitnesstracker.Meal


data class Meal(
    val id: Int,
    val name: String,
    val description: String,
    var coverImage: Int? = null, // File path or URI to cover image
    val calories: Int,
    val instructions: List<MealStep> = emptyList() // Steps for each workout
)

data class MealStep(
    val stepNumber: Int,
    val description: String,
    var image: Int? // File path or URI to step image
)

