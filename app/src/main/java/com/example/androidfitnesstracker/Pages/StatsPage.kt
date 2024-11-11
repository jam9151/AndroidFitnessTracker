package com.example.androidfitnesstracker.Pages

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidfitnesstracker.User.UserDatabaseHelper
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import com.example.androidfitnesstracker.User.UserSessionManager
import com.example.androidfitnesstracker.ui.theme.EncapsulatedSection


@Composable
fun StatsPage(dbHelper: UserDatabaseHelper, sessionManager: UserSessionManager) {
    val userId = sessionManager.getUserId()
    Log.d("StatsPage", "Fetched userId: $userId")
    // Fetch data from the database
    val caloriesData = remember { mutableStateOf(List(10) { 0 }) }
    val stepsData = remember { mutableStateOf(List(10) { 0 }) }
    val distanceData = remember { mutableStateOf(List(10) { 0f }) }
    val yearlyData = remember { mutableStateOf(List(12) { Triple(0f, 0, 0f) }) }

    LaunchedEffect(Unit) {
        userId?.let {
            caloriesData.value = dbHelper.getLast10DaysCalories(it).reversed()
            Log.d("StatsPage", "Calories Data: ${caloriesData.value}") // Log here
            stepsData.value = dbHelper.getLast10DaysSteps(it).reversed()
            Log.d("StatsPage", "Steps Data: ${stepsData.value}") // Log here
            distanceData.value = dbHelper.getLast10DaysDistance(it).reversed()
            Log.d("StatsPage", "Distance Data: ${distanceData.value}") // Log here
            yearlyData.value = dbHelper.getYearlyData(it)
            Log.d("StatsPage", "Yearly Data: ${yearlyData.value}") // Log here
        }
        /*userId?.let {
            caloriesData.value = List(10) { (100..500).random() }
            stepsData.value = List(10) { (5000..15000).random() }
            distanceData.value = List(10) { (1..10).random().toFloat() }
            yearlyData.value = List(12) { Triple((0..100).random().toFloat(), it + 1, (0..50).random().toFloat()) }
        }*/
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Your Stats",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        EncapsulatedSection(
            title = "Calories Burned (Last 10 Days)",
            modifier = Modifier.height(500.dp)
        ) {
            BarGraphWithAxes(
                data = caloriesData.value.map { it.toFloat() },
                barColor = Color.Red,
                bottomJustified = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Spacer for visual separation

        EncapsulatedSection(
            title = "Steps Taken (Last 10 Days)",
            modifier = Modifier.height(500.dp)
        ) {
            BarGraphWithAxes(
                data = stepsData.value.map { it.toFloat() },
                barColor = Color.Blue,
                bottomJustified = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        EncapsulatedSection(
            title = "Distance Traveled (Last 10 Days)",
            modifier = Modifier.height(500.dp)
        ) {
            BarGraphWithAxes(
                data = distanceData.value,
                barColor = Color.Green,
                bottomJustified = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        EncapsulatedSection(
            title = "Yearly Progress",
            modifier = Modifier.height(500.dp)
        ) {
            LineGraphWithAxes(data = yearlyData.value)
        }
    }
}

@Composable
fun BarGraph(
    data: List<Float>,
    barColor: Color,
    bottomJustified: Boolean = false
) {

    val maxData = (data.maxOrNull() ?: 1f)
    val scale = 400.dp / maxData // Scale to fit within 200dp height

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = if (bottomJustified) Alignment.Bottom else Alignment.Top
    ) {
        data.forEach { value ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = value.toInt().toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height((value * scale.value).dp.coerceAtLeast(2.dp)) // Convert the scaled value back to Dp
                        .background(barColor)
                )
            }
        }
    }
}

@Composable
fun BarGraphWithAxes(
    data: List<Float>,
    barColor: Color,
    bottomJustified: Boolean = false
) {
    val maxData = (data.maxOrNull() ?: 1f)
    val graphHeight = 350.dp
    val scale = with(LocalDensity.current) { (graphHeight.toPx() / maxData) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Total height for the graph
    ) {
        // Axes and Notch
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(graphHeight)
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp)
        ) {
            // Draw Y-axis
            drawLine(
                color = Color.Black,
                start = Offset(x = 50f, y = size.height),
                end = Offset(x = 50f, y = 0f),
                strokeWidth = 4f
            )
            // Draw X-axis
            drawLine(
                color = Color.Black,
                start = Offset(x = 50f, y = size.height),
                end = Offset(x = size.width, y = size.height),
                strokeWidth = 4f
            )

            // Draw Max Value Notch
            val maxYPos = size.height - (maxData * scale)
            drawLine(
                color = Color.Black,
                start = Offset(x = 40f, y = maxYPos),
                end = Offset(x = 60f, y = maxYPos),
                strokeWidth = 2f
            )
        }

        // Max Value Text
        Text(
            text = maxData.toInt().toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 30.dp, top = 4.dp)
        )

        // Bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(graphHeight) // Bars align with the graph height
                .padding(start = 50.dp) // Align bars with the Y-axis
                .align(Alignment.BottomStart), // Ensure bars align with the bottom of the section
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = if (bottomJustified) Alignment.Bottom else Alignment.Top
        ) {
            data.forEach { value ->
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height((value * scale / LocalDensity.current.density).dp.coerceAtLeast(2.dp)) // Convert scaled value to Dp
                        .background(barColor)
                )
            }
        }
    }
}






@Composable
fun LineGraph(data: List<Triple<Float, Int, Float>>) {
    Log.d("LineGraph", "Data received: $data")
    if (data.isEmpty()) {
        Log.e("LineGraph", "Error: Received empty data")
    }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(8.dp)
    ) {
        val width = size.width
        val height = size.height
        val maxCalories = data.maxOfOrNull { it.first } ?: 1f
        val maxSteps = data.maxOfOrNull { it.second } ?: 1
        val maxDistance = data.maxOfOrNull { it.third } ?: 1f

        val caloriesPoints = data.mapIndexed { index, (calories, _, _) ->
            Offset(
                x = index * (width / data.size),
                y = height - (calories / maxCalories) * height
            )
        }

        val stepsPoints = data.mapIndexed { index, (_, steps, _) ->
            Offset(
                x = index * (width / data.size),
                y = height - (steps.toFloat() / maxSteps) * height
            )
        }

        val distancePoints = data.mapIndexed { index, (_, _, distance) ->
            Offset(
                x = index * (width / data.size),
                y = height - (distance / maxDistance) * height
            )
        }

        drawPath(
            path = Path().apply { moveTo(caloriesPoints.first().x, caloriesPoints.first().y) }
                .apply { caloriesPoints.forEach { lineTo(it.x, it.y) } },
            color = Color.Red,
            style = Stroke(width = 4f)
        )

        drawPath(
            path = Path().apply { moveTo(stepsPoints.first().x, stepsPoints.first().y) }
                .apply { stepsPoints.forEach { lineTo(it.x, it.y) } },
            color = Color.Blue,
            style = Stroke(width = 4f)
        )

        drawPath(
            path = Path().apply { moveTo(distancePoints.first().x, distancePoints.first().y) }
                .apply { distancePoints.forEach { lineTo(it.x, it.y) } },
            color = Color.Green,
            style = Stroke(width = 4f)
        )
    }
}

@Composable
fun LineGraphWithAxes(
    data: List<Triple<Float, Int, Float>> // Contains calories, distance, and steps
) {
    if (data.isEmpty()) return

    val calories = data.map { it.first }.reversed()
    val distance = data.map { it.third }.reversed()
    val steps = data.map { it.second.toFloat() }.reversed()

    val maxY = maxOf(
        calories.maxOrNull() ?: 1f,
        distance.maxOrNull() ?: 1f,
        steps.maxOrNull() ?: 1f
    ) // Maximum value across all Y-axis data

    val graphHeight = 350.dp
    val scaleY = with(LocalDensity.current) { graphHeight.toPx() / maxY } // Scale for Y-axis
    val scaleX = with(LocalDensity.current) { 300.dp.toPx() / (calories.size - 1) } // Scale for X-axis

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        // Draw the axes and lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Draw Y-axis
            drawLine(
                color = Color.Black,
                start = Offset(50f, 0f),
                end = Offset(50f, height),
                strokeWidth = 3f
            )

            // Draw X-axis
            drawLine(
                color = Color.Black,
                start = Offset(50f, height - 50f),
                end = Offset(width, height - 50f),
                strokeWidth = 3f
            )

            // Add Y-axis notches
            val notchStep = maxY / 2
            for (i in 0..2) {
                val y = height - 50f - (scaleY * notchStep * i)
                drawLine(
                    color = Color.Black,
                    start = Offset(45f, y),
                    end = Offset(55f, y),
                    strokeWidth = 3f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    (notchStep * i).toInt().toString(),
                    10f,
                    y + 5f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 24f
                    }
                )
            }

            // Plot calories
            drawLineGraph(calories, scaleX, scaleY, height, Color.Red)

            // Plot distance
            drawLineGraph(distance, scaleX, scaleY, height, Color.Green)

            // Plot steps
            drawLineGraph(steps, scaleX, scaleY, height, Color.Blue)
        }
    }
}

fun DrawScope.drawLineGraph(
    data: List<Float>,
    scaleX: Float,
    scaleY: Float,
    graphHeight: Float,
    color: Color
) {
    if (data.size < 2) return // Need at least 2 points to draw a line

    val points = data.mapIndexed { index, value ->
        Offset(50f + index * scaleX, graphHeight - 50f - (value * scaleY))
    }

    points.zipWithNext().forEach { (start, end) ->
        drawLine(
            color = color,
            start = start,
            end = end,
            strokeWidth = 5f
        )
    }
}


