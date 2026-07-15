package com.jmcoding.dots

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * Refreshes the widget once, then reschedules itself for the following midnight.
 * Call scheduleNextMidnightRefresh() once (e.g. from your Application.onCreate,
 * or from the widget's onEnabled) to start the chain.
 */
class YearProgressWidgetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        YearProgressWidget().updateAll(applicationContext)
        scheduleNextMidnightRefresh(applicationContext)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "year_progress_widget_midnight_refresh"

        fun scheduleNextMidnightRefresh(context: Context) {
            val now = LocalDateTime.now()
            val nextMidnight = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)
            val delay = Duration.between(now, nextMidnight).toMillis()

            val request = OneTimeWorkRequestBuilder<YearProgressWidgetWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }
    }
}