package com.example.androidfitnesstracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    authManager: AuthManager,
    onLogin: (String, String) -> Unit,
    onSignUp: (
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
    ) -> Unit
) {
    // State variables for user inputs
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("General User") }
    var age by remember { mutableStateOf(0) }
    var weight by remember { mutableStateOf(0f) }
    var gender by remember { mutableStateOf("Other") }
    var height by remember { mutableStateOf<Float?>(null) }
    var diet by remember { mutableStateOf<String?>(null) }
    var membershipType by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isSignup by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Adds spacing between items
    ) {
        item {
            if (isSignup) {
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = if (age == 0) "" else age.toString(),
                    onValueChange = { age = it.toIntOrNull() ?: 0 },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = if (weight == 0f) "" else weight.toString(),
                    onValueChange = { weight = it.toFloatOrNull() ?: 0f },
                    label = { Text("Weight") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = height?.toString() ?: "",
                    onValueChange = { height = it.toFloatOrNull() },
                    label = { Text("Height") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = diet ?: "",
                    onValueChange = { diet = it },
                    label = { Text("Diet") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = membershipType,
                    onValueChange = { membershipType = it },
                    label = { Text("Membership Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Username, password, and confirm password fields
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            if (isSignup) {
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button for login or signup
            Button(onClick = {
                if (isSignup) {
                    if (!authManager.isEmailValid(email)) {
                        errorMessage = "Invalid email format"
                    } else if (!authManager.doPasswordsMatch(password, confirmPassword)) {
                        errorMessage = "Passwords do not match"
                    } else {
                        onSignUp(
                            username, password, email, firstName, lastName, userType, age, weight,
                            gender, height, diet, membershipType, address
                        )
                    }
                } else {
                    if (!authManager.login(username, password)) {
                        errorMessage = "Username or password not correct"
                    } else {
                        onLogin(username, password)
                        errorMessage = null // Clear error if login is successful
                    }
                }
            }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(text = if (isSignup) "Sign Up" else "Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle between login and signup mode
            Button(onClick = { isSignup = !isSignup }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(text = if (isSignup) "Already have an account? Login" else "Don't have an account? Sign Up")
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = androidx.compose.ui.graphics.Color.Red)
            }
        }
    }
}


