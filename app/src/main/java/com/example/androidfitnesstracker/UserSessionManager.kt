package com.example.androidfitnesstracker

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class UserSessionManager(context: Context) {

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
    fun loginUser(username: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("username", username)
        editor.apply()  // Apply changes
    }

    // Check if the user is logged in
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
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
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.remove("username")
        editor.remove("email")
        editor.apply()  // Apply changes
    }
}
