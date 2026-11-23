package com.example.insects.ui.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.insects.data.repository.GoldRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GoldRateUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: GoldRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            repository.refreshGoldRate(force = true)

            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
            val componentName = ComponentName(applicationContext, GoldRateWidget::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(componentName)

            for (widgetId in widgetIds) {
                GoldRateWidget.updateAppWidget(applicationContext, appWidgetManager, widgetId)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}