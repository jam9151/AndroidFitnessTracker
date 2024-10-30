package com.example.androidfitnesstracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidfitnesstracker.ui.theme.AndroidFitnessTrackerTheme
import com.example.androidfitnesstracker.AuthManager
import com.example.androidfitnesstracker.AuthScreen
import com.example.androidfitnesstracker.UserDatabaseHelper



class LoginActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = UserDatabaseHelper.getInstance(this)
        authManager = AuthManager(dbHelper)

        // Initialize UserSessionManager to check login state
        val sessionManager = UserSessionManager(this)

        // Check if the user is logged in
        if (sessionManager.isLoggedIn()) {
            // User is already logged in, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Close LoginActivity so the user can't go back to it
        } else {
            // User is not logged in, show the login screen

            setContent {
                AndroidFitnessTrackerTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)  // You can apply custom padding directly
                    ) {
                        AuthScreen(
                            authManager = authManager,
                            onLogin = { username, password ->
                                if (authManager.login(username, password)) {

                                    //on successful login, save session and navigate to main page
                                    sessionManager.loginUser(username)
                                    navigateToMainActivity()
                                } else {
                                    // Handle login failure
                                }
                            },
                            onSignUp = { username, password, email ->
                                val result = authManager.signup(username, password, email)
                                if (result == SignUpResult.SUCCESS) {

                                    //on successful signup, save session and navigate to main page
                                    sessionManager.loginUser(username)
                                    navigateToMainActivity()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()  // Close LoginActivity
    }
}

