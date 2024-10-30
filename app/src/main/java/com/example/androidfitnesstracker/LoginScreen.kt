package com.example.androidfitnesstracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
    authManager: AuthManager, // Pass the auth manager as a parameter
    onLogin: (String, String) -> Unit,
    onSignUp: (String, String, String) -> Unit
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isSignup by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) } // Updated to hold error messages

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
            BasicTextField(
                value = email,
                onValueChange = {
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
                    passwordFocusRequester.requestFocus()
                })
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        BasicTextField(
            value = username,
            onValueChange = {
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
                passwordFocusRequester.requestFocus()
            })
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = password,
            onValueChange = {
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
                    if (isSignup) confirmPasswordFocusRequester.requestFocus()
                },
                onDone = {
                    onSignUp(username, password, email)
                }
            )
        )

        if (isSignup) {
            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = confirmPassword,
                onValueChange = {
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
                        if (!authManager.isEmailValid(email)) {
                            errorMessage = "Invalid email format"
                        } else if (!authManager.doPasswordsMatch(password, confirmPassword)) {
                            errorMessage = "Passwords do not match"
                        } else {
                            onSignUp(username, password, email)
                        }
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (isSignup) {
                    if (!authManager.isEmailValid(email)) {
                        errorMessage = "Invalid email format"
                    } else if (!authManager.doPasswordsMatch(password, confirmPassword)) {
                        errorMessage = "Passwords do not match"
                    } else {
                        onSignUp(username, password, email)
                    }
                } else {
                    // Attempt login and display error message if login fails
                    if (!authManager.login(username, password)) {
                        errorMessage = "Username or password not correct"
                    } else {
                        onLogin(username, password)
                        errorMessage = null // Clear error if login is successful
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isSignup) "Sign Up" else "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { isSignup = !isSignup },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isSignup) "Already have an account? Login" else "Don't have an account? Sign Up")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}