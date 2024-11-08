package com.example.androidfitnesstracker.Pages

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidfitnesstracker.ui.theme.DualEncapsulatedSection
import com.example.androidfitnesstracker.ui.theme.EncapsulatedSection
import com.example.androidfitnesstracker.Activities.LoginActivity
import com.example.androidfitnesstracker.Membership.getRandomAdvertisement
import com.example.androidfitnesstracker.ui.theme.ProgressBarWithLabel
import com.example.androidfitnesstracker.R
import com.example.androidfitnesstracker.ui.theme.SquareEncapsulatedSection
import com.example.androidfitnesstracker.Membership.SubscriptionStatus
import com.example.androidfitnesstracker.User.UserActivityManager
import com.example.androidfitnesstracker.User.UserDatabaseHelper
import com.example.androidfitnesstracker.User.UserSessionManager
import com.example.androidfitnesstracker.Workout.DailySummary
import com.example.androidfitnesstracker.ui.theme.gradientBackground
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
    Log.d("DailySummaryCheck", "Calories burned: ${dailySummary.value?.calories}")

    val context = LocalContext.current


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
            .statusBarsPadding()
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
                            // Display Calories Progress
                            ProgressBarWithLabel(
                                progress = dailySummary.value!!.caloriesPercentage / 100f,
                                color = Color(0xFFFFA726), // Orange color for calories
                                label = "${dailySummary.value!!.calories.toInt()} kcal"
                            )

                            // Display Steps Progress
                            ProgressBarWithLabel(
                                progress = dailySummary.value!!.stepsPercentage / 100f,
                                color = Color(0xFF66BB6A), // Green color for steps
                                label = "${dailySummary.value!!.steps} steps"
                            )

                            // Display Distance Progress
                            ProgressBarWithLabel(
                                progress = dailySummary.value!!.distancePercentage / 100f,
                                color = Color(0xFF42A5F5), // Blue color for distance
                                label = "${dailySummary.value!!.distance} km"
                            )

                        } else {
                            Text(
                                text = "Loading daily summary...",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }


            // Workout Section
            item {
                EncapsulatedSection(
                    title = "Daily Workout",
                    onClick = {  navController.navigate("workoutList") }
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
                        val advertisement = remember { getRandomAdvertisement(context) }

                        SquareEncapsulatedSection(
                            title = "",
                            onClick = {
                                // Define action for right section
                            },
                            padding = 2
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)  // Keep square shape
                                    .clip(RoundedCornerShape(8.dp))  // Optional rounding for aesthetics
                                    .padding(0.dp)  // Slight padding to reduce the border effect
                            ) {
                                Image(
                                    painter = painterResource(id = advertisement.imageResId),
                                    contentDescription = "Advertisement",
                                    modifier = Modifier.fillMaxSize()
                                )
                                // Overlay for description and price at the top-right corner
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(2.dp) // Padding around the Column itself
                                ) {
                                    // Box for the description
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color.Black.copy(alpha = 0.7f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp) // Padding inside the text box
                                    ) {
                                        Text(
                                            text = advertisement.description,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1, // Limit to 1 line
                                            overflow = TextOverflow.Ellipsis // Add ellipsis if text overflows
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp)) // Spacing between blocks

                                    // Box for the price
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color.Black.copy(alpha = 0.7f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp) // Padding inside the text box
                                    ) {
                                        Text(
                                            text = "$${advertisement.price}",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            maxLines = 1, // Limit to 1 line
                                            overflow = TextOverflow.Ellipsis // Add ellipsis if text overflows
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }

            item {
                EncapsulatedSection(
                    title = "Leaderboard",
                    onClick = {
                        navController.navigate("leaderboardPage")
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Sample leaderboard entries - replace with dynamic content as needed
                        Text("1. User123 - 1500 points", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("2. Player456 - 1450 points", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("3. Gamer789 - 1400 points", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        // Additional leaderboard entries can be added here
                    }
                }
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



