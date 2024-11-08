// RegistrationPage.kt
package com.example.androidfitnesstracker.Pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.androidfitnesstracker.Auth.AuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationPage(
    authManager: AuthManager,
    onRegister: (String, String, String, String, String, Int, Float, String, Float, String) -> Unit
) {
    // State variables for navigation and user input
    var isFirstPage by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    // Page 1 input fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Page 2 input fields
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var membershipType by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dropdown state variables
    val genderOptions = listOf("Male", "Female")
    val membershipOptions = listOf("User", "Trainer")
    var genderExpanded by remember { mutableStateOf(false) }
    var membershipExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isFirstPage) {
            // First Page: Basic Information
            item {
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("First Name") }
                )
            }

            item {
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Last Name") }
                )
            }

            item {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Email") }
                )
            }

            item {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Username") }
                )
            }

            item {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    placeholder = { Text("Password") }
                )
            }

            item {
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    placeholder = { Text("Confirm Password") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Validate passwords match and proceed to the second page
                        if (authManager.isEmailValid(email) && password == confirmPassword && password.isNotEmpty()) {
                            // Move to the second page
                            isFirstPage = false
                            errorMessage = null
                        } else {
                            errorMessage = "Please ensure all fields are filled and passwords match."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue")
                }
            }
        } else {
            // Second Page: Additional Information
            item {
                TextField(
                    value = age,
                    onValueChange = { age = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Age") }
                )
            }

            item {
                TextField(
                    value = weight,
                    onValueChange = { weight = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Weight (lbs)") }
                )
            }

            item {
                TextField(
                    value = height,
                    onValueChange = { height = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Height (inches)") }
                )
            }

            // Gender Dropdown
            item {
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    TextField(
                        value = gender,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        placeholder = { Text("Gender") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                        },
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Membership Type Dropdown
            item {
                ExposedDropdownMenuBox(
                    expanded = membershipExpanded,
                    onExpandedChange = { membershipExpanded = !membershipExpanded }
                ) {
                    TextField(
                        value = membershipType,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        placeholder = { Text("Membership Type") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = membershipExpanded)
                        },
                    )
                    ExposedDropdownMenu(
                        expanded = membershipExpanded,
                        onDismissRequest = { membershipExpanded = false }
                    ) {
                        membershipOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    membershipType = option
                                    membershipExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isLoading = true // Show loading indicator
                        // Convert age, weight, and height to appropriate types and call onRegister
                        val parsedAge = age.toIntOrNull() ?: 0
                        val parsedWeight = weight.toFloatOrNull() ?: 0f
                        val parsedHeight = height.toFloatOrNull() ?: 0f

                        onRegister(
                            firstName,
                            lastName,
                            email,
                            username,
                            password,
                            parsedAge,
                            parsedWeight,
                            gender,
                            parsedHeight,
                            membershipType
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Register")
                    }
                }
            }
        }

        // Display error message if present
        item {
            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = androidx.compose.ui.graphics.Color.Red)
            }
        }
    }
}












