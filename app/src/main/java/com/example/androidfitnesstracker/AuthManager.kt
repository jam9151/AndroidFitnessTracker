package com.example.androidfitnesstracker

import android.util.Patterns
import java.security.MessageDigest


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

    // Simulated login method (you can later hook this up to a local database)
    fun login(username: String, password: String): Boolean {
        val hashedPassword = hashPassword(password)
        return dbHelper.checkUser(username, hashedPassword)
    }

    // Simulated signup method (for local storage)
    fun signup(username: String, password: String, email: String): Boolean {
        val hashedPassword = hashPassword(password)
        val success = dbHelper.addUser(username, hashedPassword)
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

