package com.example.androidfitnesstracker

import android.util.Patterns

class AuthManager {

    // Validate email
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validate password and confirmation
    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    // Simulated login method (you can later hook this up to a local database)
    fun login(username: String, password: String): Boolean {
        // Add your login logic here (e.g., check against stored credentials)
        return true // Simulate a successful login for now
    }

    // Simulated signup method (for local storage)
    fun signup(username: String, password: String, email: String): Boolean {
        // Add your signup logic here (e.g., store the user's credentials locally)
        return true // Simulate a successful signup
    }
}
