package com.example.androidfitnesstracker

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class UserSessionManager(context: Context) {

    private val dbHelper = UserDatabaseHelper.getInstance(context)

    // Create a MasterKey using the KTX-style API
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Create EncryptedSharedPreferences using KTX extensions
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "user_prefs",  // File name
        masterKey,     // MasterKey for encryption
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Save login details when the user successfully logs in
    fun loginUser(username: String): Boolean {
        val userId = dbHelper.getUserIdByUsername(username) // Query for userId

        // Check if userId is null before proceeding
        if (userId != null) {
            with(sharedPreferences.edit()) {
                putBoolean("isLoggedIn", true)
                putInt("userId", userId)
                putString("username", username)
                apply()
            }
            return true
        } else {
            // Handle the case where userId is not found, e.g., log an error
            // or show a message to the user if appropriate
            println("Error: userId not found for username $username")
            return false
        }
    }

    // Check if the user is logged in
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun getUserId(): Int? {
        return if (sharedPreferences.contains("userId")) {
            sharedPreferences.getInt("userId", -1)
        } else {
            null
        }
    }

    // Get the logged-in user's username
    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    // Get the logged-in user's email
    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    // Clear the user's session when logging out
    fun logoutUser() {
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            remove("userId")
            remove("username")
            remove("email")
            apply()  // Apply changes
        }
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
