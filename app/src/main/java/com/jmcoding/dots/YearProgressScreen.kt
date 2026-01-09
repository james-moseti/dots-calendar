package com.jmcoding.dots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jmcoding.dots.computeYearProgress
import java.time.LocalDate

@Composable
fun YearProgressScreen(
    today: LocalDate = LocalDate.now()
) {
    val progress = remember(today) { computeYearProgress(today) }

    val dotSize = 10.dp
    val spacing = 8.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(vertical = 24.dp)
            .semantics {
                contentDescription =
                    "Year progress: ${progress.daysPassed} days completed, ${progress.daysLeft} days left"
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = progress.year.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(40.dp))

        DotGrid(
            totalDots = progress.totalDays,
            filledDots = progress.daysPassed,
            dotSize = dotSize,
            spacing = spacing,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "${progress.daysLeft}d left • ${"%.1f".format(progress.percentComplete)}%",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DotGrid(
    totalDots: Int,
    filledDots: Int,
    dotSize: Dp,
    spacing: Dp,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(15),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        items((0 until totalDots).toList()) { index ->
            Dot(isFilled = index < filledDots, size = dotSize)
        }
    }
}

@Composable
private fun Dot(isFilled: Boolean, size: Dp) {
    val filled = MaterialTheme.colorScheme.primary
    val empty = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = if (isFilled) filled else empty,
                shape = CircleShape
            )
    )
}