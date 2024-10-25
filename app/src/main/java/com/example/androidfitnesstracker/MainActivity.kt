package com.example.androidfitnesstracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidfitnesstracker.ui.theme.AndroidFitnessTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val sessionManager = UserSessionManager(this)
        val username = sessionManager.getUsername()

        // Here you handle the core features of the app
        setContent {
            AndroidFitnessTrackerTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Welcome to the Main Page, $username!")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Log Out Button
                    Button(
                        onClick = {
                            // Log the user out
                            sessionManager.logoutUser()

                            // Navigate back to the login screen
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()  // Close MainActivity so user can't go back
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Log Out")
                    }
                }
            }
        }
    }
}





