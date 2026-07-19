package com.jmcoding.dots.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Renders the year progress dot grid into a Bitmap so the widget can display
 * pixel-identical output to the in-app Compose screen, without fighting
 * RemoteViews/Glance layout constraints for a dynamic dot count.
 */
fun drawYearProgressBitmap(
    widthPx: Int,
    heightPx: Int,
    totalDays: Int,
    filledDays: Int,
    daysLeft: Int,
    percentComplete: Float,
    backgroundColor: Int = Color.parseColor("#2C2C2E"),
    filledColor: Int = Color.parseColor("#8AB4F8"),
    emptyColor: Int = Color.parseColor("#4A4A4C"),
    subtextColor: Int = Color.parseColor("#99FFFFFF")
): Bitmap {
    val w = max(widthPx, 1)
    val h = max(heightPx, 1)
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(backgroundColor)

    val density = w / 200f // rough scale factor so text/dots scale with widget size
    val padding = 12f * density

    // --- Footer text ---
    val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = subtextColor
        typeface = Typeface.DEFAULT
        textSize = 10f * density
        textAlign = Paint.Align.CENTER
    }
    val footerHeight = footerPaint.fontSpacing

    val gridTop = padding
    val gridBottom = h - padding - footerHeight - (6f * density)
    val gridAreaHeight = max(gridBottom - gridTop, 1f)
    val gridAreaWidth = max(w - 2 * padding, 1f)

    // Pick a column count that makes the grid roughly fill the available box
    // while keeping dots reasonably square/circular.
    val idealCols = ceil(sqrt(totalDays * (gridAreaWidth / gridAreaHeight))).toInt()
    val cols = idealCols.coerceIn(7, 31)
    val rows = ceil(totalDays / cols.toFloat()).toInt()

    val spacing = 2f * density
    val dotSize = min(
        (gridAreaWidth - spacing * (cols - 1)) / cols,
        (gridAreaHeight - spacing * (rows - 1)) / rows
    ).coerceAtLeast(1f)

    val gridWidth = cols * dotSize + (cols - 1) * spacing
    val gridHeight = rows * dotSize + (rows - 1) * spacing
    val startX = (w - gridWidth) / 2f
    val startY = gridTop + (gridAreaHeight - gridHeight) / 2f

    val filledPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = filledColor }
    val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = emptyColor }
    val radius = dotSize / 2f

    for (i in 0 until totalDays) {
        val col = i % cols
        val row = i / cols
        val cx = startX + col * (dotSize + spacing) + radius
        val cy = startY + row * (dotSize + spacing) + radius
        canvas.drawCircle(cx, cy, radius, if (i < filledDays) filledPaint else emptyPaint)
    }

    // Draw footer
    val footerText = "${daysLeft}d left • ${"%.1f".format(percentComplete)}%"
    canvas.drawText(footerText, w / 2f, h - padding, footerPaint)

    return bitmap
}