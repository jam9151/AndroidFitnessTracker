package com.example.androidfitnesstracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun EncapsulatedSection(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)                   // Fixed height for uniformity
            .clip(RoundedCornerShape(12.dp))   // Rounded corners
            .background(Color(0xFFB0B0B0))     // Light grey background
            .clickable { onClick() }
            .padding(16.dp)                    // Internal padding
    ) {
        // New Box to position the SectionTitle in the top-right corner
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SectionTitle(
                title = title,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    //.padding(end = 0.dp, start = 0.dp)  // Padding for spacing
            )
        }

        // Content Row for the section, spaced below the title
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),         // Ensures content starts below the title
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            content()  // Custom content goes here
        }
    }
}





val gradientBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFF121212), Color(0xFF1E1E1E)), // Dark gray gradient
    startY = 0.0f,
    endY = Float.POSITIVE_INFINITY
)

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        color = Color.Black,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier  // Pass the modifier here
    )
}


@Composable
fun SquareEncapsulatedSection(
    title: String,
    onClick: () -> Unit = {},
    padding: Int = 16, // Define padding as an Int
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)                   // Ensures it's square
            .clip(RoundedCornerShape(12.dp))   // Rounded corners
            .background(Color(0xFFB0B0B0))     // Light grey background
            .clickable { onClick() }
            .padding(padding.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (title != "") {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp)) // Space between title and content
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                content()  // Placeholder for custom content
            }
        }
    }
}


@Composable
fun DualEncapsulatedSection(
    leftSection: @Composable () -> Unit,
    rightSection: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            leftSection()  // Left section content
        }
        Box(
            modifier = Modifier.weight(1f)
        ) {
            rightSection() // Right section content
        }
    }
}



@Composable
fun GoBackButton(navController: NavController, modifier: Modifier = Modifier) {
    Button(
        onClick = { navController.popBackStack() },
        modifier = modifier
    ) {
        Text("Go Back")
    }
}



