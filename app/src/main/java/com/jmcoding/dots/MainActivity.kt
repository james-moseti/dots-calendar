package com.jmcoding.dots


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jmcoding.dots.ui.theme.DotsTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DotsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    YearProgressScreen()
                }
            }
        }
    }
}

@Composable
fun YearProgressScreen() {
    val currentDate = LocalDate.now()
    val year = currentDate.year
    val startOfYear = LocalDate.of(year, 1, 1)
    val endOfYear = LocalDate.of(year, 12, 31)

    val totalDays = ChronoUnit.DAYS.between(startOfYear, endOfYear) + 1
    val daysPassed = ChronoUnit.DAYS.between(startOfYear, currentDate)
    val daysLeft = ChronoUnit.DAYS.between(currentDate, endOfYear)
    val percentComplete = String.format("%.1f", daysPassed.toFloat() / totalDays.toFloat() * 100)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Year text
        Text(
            text = year.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Dots grid
        DotGrid(
            totalDots = totalDays.toInt(),
            filledDots = daysPassed.toInt(),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats at bottom
        Text(
            text = "${daysLeft}d left • $percentComplete%",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DotGrid(
    totalDots: Int,
    filledDots: Int,
    modifier: Modifier = Modifier
) {
    val columns = 15
    val rows = ceil(totalDots.toFloat() / columns).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (col in 0 until columns) {
                    val dotIndex = row * columns + col
                    if (dotIndex < totalDots) {
                        Dot(isFilled = dotIndex < filledDots)
                    }
                }
            }
        }
    }
}

@Composable
fun Dot(isFilled: Boolean) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(
                color = if (isFilled) Color(0xFF6B7FFF) else Color.DarkGray,
                shape = CircleShape
            )
    )
}

@Composable
fun DotsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF6B7FFF),
            background = Color.Black,
            surface = Color.Black
        ),
        content = content
    )
}