package com.example.androidfitnesstracker.Pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.androidfitnesstracker.User.UserDatabaseHelper
import java.util.*

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import com.example.androidfitnesstracker.Membership.SubscriptionStatus
import androidx.compose.material3.Text



@Composable
fun UpgradePage(
    dbHelper: UserDatabaseHelper,
    userId: Int,  // Pass in the user ID to update the subscription
    onUpgradeSuccess: () -> Unit // Callback for navigation after successful upgrade
) {
    // Define mutable states for each field and error messages
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val creditCardNumber = remember { mutableStateOf("") }
    val cvv = remember { mutableStateOf("") }
    val zipCode = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Upgrade to Premium",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Benefits
        Text(
            text = "As a Premium member, you’ll unlock a suite of exclusive benefits:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // List of Benefits
        val benefits = listOf(
            "Ad-free experience",
            "Access to custom diet plans",
            "Personal trainer guidance",
            "Priority customer support",
            "Exclusive content and tutorials"
        )

        benefits.forEach { benefit ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50), // Green color for checkmark
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = benefit,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Text boxes for first and last name side-by-side
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = firstName.value,
                onValueChange = { firstName.value = it },
                label = { Text("First Name") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = lastName.value,
                onValueChange = { lastName.value = it },
                label = { Text("Last Name") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Credit card field
        OutlinedTextField(
            value = creditCardNumber.value,
            onValueChange = { creditCardNumber.value = it },
            label = { Text("Credit Card Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // CVV and ZIP code fields on the same row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = cvv.value,
                onValueChange = { cvv.value = it },
                label = { Text("CVV") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = zipCode.value,
                onValueChange = { zipCode.value = it },
                label = { Text("ZIP Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = "3-4 digits on the back of the card",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display error message if validation fails
        errorMessage.value?.let {
            Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Upgrade Button
        Button(onClick = {
            // Validate all inputs
            when {
                firstName.value.isBlank() -> errorMessage.value = "First name cannot be blank"
                lastName.value.isBlank() -> errorMessage.value = "Last name cannot be blank"
                creditCardNumber.value.length != 16 || !creditCardNumber.value.all { it.isDigit() } -> errorMessage.value = "Invalid credit card number"
                cvv.value.length !in 3..4 || !cvv.value.all { it.isDigit() } -> errorMessage.value = "Invalid CVV"
                zipCode.value.length != 5 || !zipCode.value.all { it.isDigit() } -> errorMessage.value = "Invalid ZIP code"
                else -> {
                    // All inputs are valid, upgrade subscription
                    errorMessage.value = ""
                    dbHelper.setSubscriptionStatus(userId, SubscriptionStatus.PREMIUM)
                    onUpgradeSuccess()  // Navigate to the next screen or show success message
                }
            }
        }) {
            Text("Confirm Upgrade")
        }
    }
}

