package com.example.androidfitnesstracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidfitnesstracker.ui.theme.AndroidFitnessTrackerTheme

@Composable
fun AuthScreen(
    onAuthClick: (String, String, String?, Boolean) -> Unit, // Added email, isSignup flag
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isSignup by remember { mutableStateOf(false) }

    // FocusRequesters for each input field
    val emailFocusRequester = FocusRequester()
    val passwordFocusRequester = FocusRequester()
    val confirmPasswordFocusRequester = FocusRequester()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (isSignup) {
            // Email input for signup
            BasicTextField(
                value = email,
                onValueChange = {
                    // Filter out spaces, tabs, and newlines
                    email = it.replace(Regex("\\s"), "")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocusRequester),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(8.dp)) {
                        if (email.isEmpty()) {
                            Text("Email")
                        }
                        innerTextField()
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    passwordFocusRequester.requestFocus()  // Move focus to password
                })
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Username input
        BasicTextField(
            value = username,
            onValueChange = {
                // Filter out spaces, tabs, and newlines
                username = it.replace(Regex("\\s"), "")
            },
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.padding(8.dp)) {
                    if (username.isEmpty()) {
                        Text("Username")
                    }
                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                passwordFocusRequester.requestFocus()  // Move focus to password
            })
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input
        BasicTextField(
            value = password,
            onValueChange = {
                // Filter out spaces, tabs, and newlines
                password = it.replace(Regex("\\s"), "")
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
            visualTransformation = PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.padding(8.dp)) {
                    if (password.isEmpty()) {
                        Text("Password")
                    }
                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = if (isSignup) ImeAction.Next else ImeAction.Done),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (isSignup) confirmPasswordFocusRequester.requestFocus()  // Move focus to confirm password in signup mode
                },
                onDone = {
                    onAuthClick(username, password, email, isSignup)
                }
            )
        )

        if (isSignup) {
            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password input for signup
            BasicTextField(
                value = confirmPassword,
                onValueChange = {
                    // Filter out spaces, tabs, and newlines
                    confirmPassword = it.replace(Regex("\\s"), "")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(confirmPasswordFocusRequester),
                visualTransformation = PasswordVisualTransformation(),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(8.dp)) {
                        if (confirmPassword.isEmpty()) {
                            Text("Confirm Password")
                        }
                        innerTextField()
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onAuthClick(username, password, email, true)
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Auth button (Login/Signup)
        Button(
            onClick = {
                if (isSignup) {
                    onAuthClick(username, password, email, true)
                } else {
                    onAuthClick(username, password, null, false)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isSignup) "Sign Up" else "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Switch between login and signup
        Button(
            onClick = { isSignup = !isSignup },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isSignup) "Already have an account? Login" else "Don't have an account? Sign Up")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AndroidFitnessTrackerTheme {
        AuthScreen { _, _, _, _ -> }
    }
}


