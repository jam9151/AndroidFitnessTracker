package com.example.androidfitnesstracker

data class DailySummary(
    val steps: Int,
    val calories: Float,
    val distance: Float,
    val caloriesPercentage: Float,
    val stepsPercentage: Float,
    val distancePercentage: Float
)
