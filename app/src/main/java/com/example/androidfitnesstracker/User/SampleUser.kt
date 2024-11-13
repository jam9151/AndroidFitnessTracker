package com.example.androidfitnesstracker.User

import android.content.Context
import android.util.Log
import com.example.androidfitnesstracker.Membership.SubscriptionStatus
import java.util.Calendar
import kotlin.random.Random

data class SampleUser(
    val username: String = "user",
    val password: String = "pass", // This will be hashed
    val email: String = "user@example.com",
    val firstName: String = "John",
    val lastName: String = "Doe",
    val subscriptionStatus: SubscriptionStatus = SubscriptionStatus.FREE
)

fun generateHistoricalData(userId: Int, dbHelper: UserDatabaseHelper) {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val random = Random.Default

    // Generate data for the past 365 days
    for (i in 1..365) {
        calendar.add(Calendar.DAY_OF_YEAR, -1) // Move one day back

        val timestamp = calendar.timeInMillis
        val steps = random.nextInt(4000, 10000) // Average daily steps
        val calories = random.nextInt(1500, 2500) // Average daily calories burned
        val distance = random.nextFloat() * (7 - 2.5f) + 2.5f // Average daily distance (2.5 to 7 km)

        dbHelper.insertActivityRecord(
            userId = userId,
            timestamp = timestamp,
            steps = steps,
            calories = calories,
            distance = distance
        )
        Log.d("DatabaseInsert", "Inserting activity record: steps=$steps, calories=$calories, distance=$distance")
    }
}

val SampleUser1 = SampleUser()
val SampleUser2 = SampleUser("user2", "pass2", "user2@example.com", "Mike", "Smith")
val SampleUser3 = SampleUser("user3", "pass3", "user3@example.com", "Andy", "Richardson")
val SampleUser4 = SampleUser("user4", "pass4", "user4@example.com", "Ron", "Johnson")
val SampleUser5 = SampleUser("user5", "pass5", "user5@example.com", "Jordan", "Gerstner")

