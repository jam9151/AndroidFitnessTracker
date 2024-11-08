package com.example.androidfitnesstracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class UserActivityManager(
    private val dbHelper: UserDatabaseHelper,
    private val sessionManager: UserSessionManager // Added sessionManager for user ID retrieval
) {

    fun addActivityRecord(userId: Int, timestamp: Long, steps: Int, calories: Int, distance: Float): Long {
        return dbHelper.insertActivityRecord(userId, timestamp, steps, calories, distance)
    }

    fun getDailySummary(userId: Int, date: Long): DailySummary {
        return dbHelper.getDailySummary(userId, date)
    }

    fun logWorkoutCompletion(workout: Workout) {
        val userId = sessionManager.getUserId() ?: return  // Retrieve user ID or return if not logged in

        val timestamp = System.currentTimeMillis()  // Use current time as timestamp
        val steps = workout.steps
        val calories = workout.calories
        val distance = workout.distance

        // Insert the workout activity for the current day
        addActivityRecord(userId, timestamp, steps, calories, distance)
        Log.d("DatabaseInsert", "Logged workout with calories: ${workout.calories}")
    }

}
