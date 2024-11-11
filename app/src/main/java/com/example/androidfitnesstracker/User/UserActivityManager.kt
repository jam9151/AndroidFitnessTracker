package com.example.androidfitnesstracker.User

import android.util.Log
import com.example.androidfitnesstracker.Meal.Meal
import com.example.androidfitnesstracker.Workout.Workout
import com.example.androidfitnesstracker.Workout.DailySummary

class UserActivityManager(
    private val dbHelper: UserDatabaseHelper,
    private val sessionManager: UserSessionManager // Added sessionManager for user ID retrieval
) {

    private fun addActivityRecord(userId: Int, timestamp: Long, steps: Int, calories: Int, distance: Float): Long {
        return dbHelper.insertActivityRecord(userId, timestamp, steps, calories, distance)
    }

    fun getDailySummary(userId: Int): DailySummary {
        return dbHelper.getDailySummary(userId)
    }

    fun logWorkoutCompletion(workout: Workout) {
        val userId = sessionManager.getUserId() ?: return  // Retrieve user ID or return if not logged in

        val timestamp = System.currentTimeMillis()  // Use current time as timestamp
        val steps = workout.steps
        val calories = workout.calories
        val distance = workout.distance

        // Insert the workout activity for the current day
        addActivityRecord(userId, timestamp, steps, calories, distance)
        Log.d("DatabaseInsert", "Logged workout with calories: ${workout.calories} and this timestamp: $timestamp")
    }

    fun logMealCompletion(it: Meal) {
        //TODO - give user points or something
    }


}
