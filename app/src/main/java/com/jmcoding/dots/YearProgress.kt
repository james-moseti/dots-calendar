package com.jmcoding.dots

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class YearProgress(
    val year: Int,
    val totalDays: Int,
    val daysPassed: Int,
    val daysLeft: Int,
    val percentComplete: Float
)

fun computeYearProgress(today: LocalDate): YearProgress {
    val year = today.year
    val start = LocalDate.of(year, 1, 1)
    val end = LocalDate.of(year, 12, 31)

    val total = (ChronoUnit.DAYS.between(start, end) + 1).toInt()

    val passed = ChronoUnit.DAYS.between(start, today).toInt().coerceIn(0, total)
    val left = ChronoUnit.DAYS.between(today, end).toInt().coerceAtLeast(0)

    val percent = (passed.toFloat() / total.toFloat()) * 100f

    return YearProgress(
        year = year,
        totalDays = total,
        daysPassed = passed,
        daysLeft = left,
        percentComplete = percent
    )
}
