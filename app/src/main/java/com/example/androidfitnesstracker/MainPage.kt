package com.example.androidfitnesstracker

import android.content.Intent
import android.graphics.fonts.FontStyle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainPage(
    navController: NavController,
    userActivityManager: UserActivityManager,
    sessionManager: UserSessionManager,
    dbHelper: UserDatabaseHelper
) {
    val userId = sessionManager.getUserId() ?: return // we would never get here anyways if userID were null
    val fullName = remember { dbHelper.getFullName(userId) }
    val firstName = remember { dbHelper.getFirstName(userId) }
    //val userSubscriptionStatus = remember { dbHelper.getSubscriptionStatus(userId) } want this to be dynamically updated

    val currentDate = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    val dailySummary = remember { mutableStateOf<DailySummary?>(null) }

    LaunchedEffect(userId) {
        val todayTimestamp = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        dailySummary.value = userActivityManager.getDailySummary(userId, todayTimestamp)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)  // Apply gradient background here
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // Date display
            item {
                Column() {
                    Text(
                        text = currentDate,
                        color = Color.White,
                        fontSize = 24.sp,                    // Increase font size for readability
                        fontWeight = FontWeight.Medium,       // Medium weight for a smooth appearance
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Text(
                        text = "Welcome, $firstName",
                        color = Color.White,
                        fontSize = 16.sp,                // Smaller font size
                        fontWeight = FontWeight.Normal,    // Non-bold font weight
                    )
                }
            }

            // Daily Summary Section
            item {
                EncapsulatedSection(
                    title = "Daily Summary",
                    onClick = {}  // No navigation, this is a display-only section
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (dailySummary.value != null) {
                            Text(
                                text = "Calories Burned: ${dailySummary.value!!.calories}",
                                color = androidx.compose.ui.graphics.Color.White
                            )
                            Text(
                                text = "Steps: ${dailySummary.value!!.steps}",
                                color = androidx.compose.ui.graphics.Color.White
                            )
                            Text(
                                text = "Distance: ${dailySummary.value!!.distance} km",
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        } else {
                            Text(
                                text = "Loading daily summary...",
                                color = androidx.compose.ui.graphics.Color.Gray
                            )
                        }
                    }
                }
            }


            // Workout Section
            item {
                EncapsulatedSection(
                    title = "Daily Workout",
                    onClick = { navController.navigate("workoutPage") }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.workout_clipart),
                        contentDescription = "Workout Image",
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Daily workout completed", color = Color.DarkGray)
                }
            }

            // Stats Section
            item {
                EncapsulatedSection(
                    title = "Track Your Progress",
                    onClick = { navController.navigate("statsPage") }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.stats_clipart),
                        contentDescription = "Stats Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            }

            item {
                DualEncapsulatedSection(
                    leftSection = {
                        SquareEncapsulatedSection(
                            title = if (dbHelper.getSubscriptionStatus(userId) == SubscriptionStatus.FREE) "Upgrade" else "My Diet",
                            onClick = {
                                if (dbHelper.getSubscriptionStatus(userId) == SubscriptionStatus.FREE) {
                                    navController.navigate("mySubscription")
                                } else {
                                    navController.navigate("mealPlan")
                                }
                            }
                        ) {
                            Text(
                                text = if (dbHelper.getSubscriptionStatus(userId) == SubscriptionStatus.FREE)
                                    "Upgrade to Premium for Customized Diet Plans"
                                else
                                    "View My Diet Plan",
                                color = Color.Black
                            )
                        }
                    },
                    rightSection = {
                        SquareEncapsulatedSection(
                            title = "Placeholder",
                            onClick = {
                                // Define action for right section
                            }
                        ) {
                            Text("Right Section Content", color = Color.Black)
                        }
                    }
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            sessionManager.logoutUser()  // Clear the session
                            val intent = Intent(navController.context, LoginActivity::class.java)
                            navController.context.startActivity(intent)
                            (navController.context as? ComponentActivity)?.finish()  // Close MainActivity
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Text("Log Out")
                    }
                }
            }
        }
    }
}



