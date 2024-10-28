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

    // Simulated login method (you can later hook this up to a local database)
    fun login(username: String, password: String): Boolean {
        val hashedPassword = hashPassword(password)
        return dbHelper.checkUser(username, hashedPassword)
    }

    //signup method for local storage
    fun signup(username: String, password: String, email: String): SignUpResult {
        if (dbHelper.isUsernameTaken(username))
        {
            return SignUpResult.USERNAME_TAKEN
        } else if (dbHelper.isEmailTaken(email))
        {
            return SignUpResult.EMAIL_TAKEN
        }

        val hashedPassword = hashPassword(password)
        val success = dbHelper.addUser(username, hashedPassword, email)
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

