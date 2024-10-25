package com.example.androidfitnesstracker

import android.util.Patterns

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
    fun signup(username: String, password: String, email: String): Boolean {
        val success = dbHelper.addUser(username, password)
        return success != -1L
    }

    fun getEmail(username: String): String? {
        val db = dbHelper.readableDatabase

        //fetch email based on username
        val cursor = db.rawQuery(
            "SELECT email FROM users WHERE username = ?",
            arrayOf(username)
        )

        var email: String? = null
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
        }

        cursor.close()
        return email  // returns email if found, otherwise null
    }
}

