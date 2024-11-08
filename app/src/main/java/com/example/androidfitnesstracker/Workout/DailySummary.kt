package com.example.androidfitnesstracker.Workout

data class DailySummary(
    val steps: Int,
    val calories: Float,
    val distance: Float,
    val caloriesPercentage: Float,
    val stepsPercentage: Float,
    val distancePercentage: Float
)
