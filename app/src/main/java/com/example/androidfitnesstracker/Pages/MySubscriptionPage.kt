package com.example.androidfitnesstracker.Pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidfitnesstracker.Membership.SubscriptionStatus
import com.example.androidfitnesstracker.User.UserDatabaseHelper
import com.example.androidfitnesstracker.User.UserSessionManager
import com.example.androidfitnesstracker.ui.theme.GoBackButton

@Composable
fun MySubscriptionPage(
    dbHelper: UserDatabaseHelper,
    sessionManager: UserSessionManager,
    onUpgradeClick: () -> Unit
) {
    // Retrieve the user ID and subscription status
    val userId = sessionManager.getUserId()
    val firstName = userId?.let { dbHelper.getFirstName(it) } ?: "User"
    val subscriptionStatus = userId?.let { dbHelper.getSubscriptionStatus(it) } ?: SubscriptionStatus.FREE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        // Greeting
        Text(
            text = "Hello, $firstName",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Divider line
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subscription Status
        Text(
            text = "Subscription status: ${subscriptionStatus.name}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content based on subscription status
        if (subscriptionStatus == SubscriptionStatus.FREE) {
            FreeSubscriptionContent(onUpgradeClick)
        } else {
            PremiumSubscriptionContent()
        }
    }
}

@Composable
fun FreeSubscriptionContent(onUpgradeClick: () -> Unit) {
    Text(
        text = "Upgrade to Premium to go ad-free and access exclusive content such as diet plans and personal trainers.",
        fontSize = 16.sp,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 16.dp)
    )

    // Upgrade button
    Button(
        onClick = onUpgradeClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = "Upgrade Now")
    }
}

@Composable
fun PremiumSubscriptionContent() {
    Text(
        text = "Thank you for being a premium member! You have access to exclusive content. You've already sold your soul; there's no way to cancel your subscription ;)",
        fontSize = 16.sp,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}


