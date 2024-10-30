package com.example.androidfitnesstracker

import android.util.Patterns
import java.security.MessageDigest

enum class SignUpResult {
    SUCCESS,
    USERNAME_TAKEN,
    EMAIL_TAKEN,
    FAILURE
}

class AuthManager(private val dbHelper: UserDatabaseHelper) {

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Validate email
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validate password and confirmation
    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    // Login method
    fun login(username: String, password: String): Boolean {
        val hashedPassword = hashPassword(password)
        return dbHelper.checkUser(username, hashedPassword)
    }

    // Expanded signup method to handle all fields from LoginScreen.kt
    fun signup(
        username: String,
        password: String,
        email: String,
        firstName: String,
        lastName: String,
        userType: String,
        age: Int,
        weight: Float,
        gender: String,
        height: Float?,
        diet: String?,
        membershipType: String,
        address: String
    ): SignUpResult {
        // Check if the username or email already exists in the database
        if (dbHelper.isUsernameTaken(username)) {
            return SignUpResult.USERNAME_TAKEN
        } else if (dbHelper.isEmailTaken(email)) {
            return SignUpResult.EMAIL_TAKEN
        }

        // Hash the password before storing
        val hashedPassword = hashPassword(password)

        // Insert the user data into the database with all additional fields
        val success = dbHelper.addUser(
            username = username,
            password = hashedPassword,
            email = email
        )

        return if (success != -1L) {
            SignUpResult.SUCCESS
        } else {
            SignUpResult.FAILURE
        }
    }

    // Optional helper method to get a user's email by their username
    fun getEmail(username: String): String? {
        return dbHelper.getEmailByUsername(username)
    }
}


