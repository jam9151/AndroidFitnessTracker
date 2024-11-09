// LoginActivity.kt
package com.example.androidfitnesstracker.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidfitnesstracker.Auth.AuthManager
import com.example.androidfitnesstracker.Auth.SignUpResult
import com.example.androidfitnesstracker.Pages.AuthScreen
import com.example.androidfitnesstracker.Pages.RegistrationPage
import com.example.androidfitnesstracker.User.UserDatabaseHelper
import com.example.androidfitnesstracker.User.UserSessionManager
import com.example.androidfitnesstracker.ui.theme.AndroidFitnessTrackerTheme

class LoginActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbHelper = UserDatabaseHelper.getInstance(this)
        authManager = AuthManager(dbHelper)

        val sessionManager = UserSessionManager(this)
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity()
        } else {
            setContent {
                AndroidFitnessTrackerTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "login") {
                            composable("login") {
                                AuthScreen(
                                    authManager = authManager,
                                    onLogin = { username, password ->
                                        if (authManager.login(username, password)) {
                                            sessionManager.loginUser(username)
                                            navigateToMainActivity()
                                        }
                                    },
                                    navigateToRegistration = {
                                        navController.navigate("registration")
                                    }
                                )
                            }
                            composable("registration") {
                                RegistrationPage(
                                    authManager = authManager,
                                    onRegister = { firstName, lastName, email, username, password, age, weight, gender, height, membershipType ->
                                        // Handle registration logic here, e.g., storing data in the database
                                        val result = authManager.signup(username, password, email)

                                        if (result == SignUpResult.SUCCESS) {
                                            if (sessionManager.loginUser(username)) {
                                                val ID = sessionManager.getUserId()

                                                if (ID != null) {
                                                    dbHelper.setFirstName(ID, firstName)
                                                    dbHelper.setLastName(ID, lastName)
                                                    dbHelper.setAge(ID, age)
                                                    dbHelper.setWeight(ID, weight)
                                                    dbHelper.setHeight(ID, height)
                                                    dbHelper.setMembershipType(ID, membershipType)
                                                    dbHelper.setGender(ID, gender)
                                                }

                                                // Navigate to MainActivity after registration is complete
                                                navigateToMainActivity()
                                            }
                                        } else {
                                            ("Registration failed: $result") // Debugging log for failure case
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}





