package com.jmcoding.dots

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.action.ActionParameters
import java.time.LocalDate
import kotlin.math.roundToInt

class YearProgressWidget : GlanceAppWidget() {

    // Renders once per size bucket rather than continuously, which keeps the
    // bitmap redraw cheap. Add/remove buckets to match the widget sizes you
    // want to support (1x1, 2x2, 4x2, 4x4, etc).
    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(110.dp, 110.dp),
            DpSize(180.dp, 110.dp),
            DpSize(180.dp, 180.dp),
            DpSize(250.dp, 180.dp),
            DpSize(250.dp, 250.dp)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        val context = LocalContext.current
        val size = LocalSize.current
        val density = context.resources.displayMetrics.density

        val progress = remember(LocalDate.now()) { computeYearProgress(LocalDate.now()) }

        val widthPx = (size.width.value * density).roundToInt()
        val heightPx = (size.height.value * density).roundToInt()

        val bitmap = remember(progress, widthPx, heightPx) {
            drawYearProgressBitmap(
                widthPx = widthPx,
                heightPx = heightPx,
                year = progress.year,
                totalDays = progress.totalDays,
                filledDays = progress.daysPassed,
                daysLeft = progress.daysLeft,
                percentComplete = progress.percentComplete
            )
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .clickable(actionRunCallback<RefreshWidgetAction>()),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(bitmap),
                contentDescription =
                    "Year progress: ${progress.daysPassed} of ${progress.totalDays} days complete, " +
                            "${progress.daysLeft} days left",
                modifier = GlanceModifier.fillMaxSize()
            )
        }
    }
}

/** Lets a tap on the widget force a redraw (handy right after midnight, or for manual refresh). */
class RefreshWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        YearProgressWidget().update(context, glanceId)
    }
}

class YearProgressWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = YearProgressWidget()
}