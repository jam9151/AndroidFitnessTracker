package com.example.androidfitnesstracker

import android.util.Patterns

enum class SignUpResult {
    SUCCESS,
    USERNAME_TAKEN,
    EMAIL_TAKEN,
    FAILURE
}

class AuthManager(private val dbHelper: UserDatabaseHelper) {

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
        return dbHelper.checkUser(username, password)
    }

    // Simulated signup method (for local storage)
    fun signup(username: String, password: String, email: String): SignUpResult {
        if (dbHelper.isUsernameTaken(username))
        {
            return SignUpResult.USERNAME_TAKEN
        } else if (dbHelper.isEmailTaken(email))
        {
            return SignUpResult.EMAIL_TAKEN
        }

        val success = dbHelper.addUser(username, password, email)
        return if (success != -1L) {
            SignUpResult.SUCCESS
        } else {
            SignUpResult.FAILURE
        }
    }

    fun getEmail(username: String): String? {
        return dbHelper.getEmailByUsername(username)
    }
}

