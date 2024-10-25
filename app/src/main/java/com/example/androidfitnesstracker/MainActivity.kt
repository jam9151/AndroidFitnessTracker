package com.example.androidfitnesstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.androidfitnesstracker.ui.theme.AndroidFitnessTrackerTheme

class MainActivity : ComponentActivity() {
    private val authManager = AuthManager() // Initialize the auth manager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidFitnessTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AuthScreen(
                        authManager = authManager,  // Pass authManager to the screen
                        onAuthClick = { username, password, email, isSignup ->
                            if (isSignup) {
                                // Handle signup logic
                                if (email != null && authManager.signup(username, password, email)) {
                                    // Signup success (e.g., navigate to another screen or show a success message)
                                } else {
                                    // Signup failed (e.g., show an error message)
                                }
                            } else {
                                // Handle login logic
                                if (authManager.login(username, password)) {
                                    // Login success (e.g., navigate to another screen or show a success message)
                                } else {
                                    // Login failed (e.g., show an error message)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}




