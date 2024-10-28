package com.example.androidfitnesstracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class UserActivityManager(private val dbHelper: UserDatabaseHelper) {

    fun addActivityRecord(userId: Int, timestamp: Long, steps: Int, calories: Float, distance: Float): Long {
        return dbHelper.insertActivityRecord(userId, timestamp, steps, calories, distance)
    }

    fun getDailySummary(userId: Int, date: Long): DailySummary {
        return dbHelper.getDailySummary(userId, date)
    }
}
