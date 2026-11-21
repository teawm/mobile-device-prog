package com.example.insects.ui.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.insects.InsectsApp

class GoldRateUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as InsectsApp
            val repository = app.goldRepository

            // Обновляем данные
            repository.refreshGoldRate(force = true)

            // Обновляем все экземпляры виджета
            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
            val widgetComponent = ComponentName(applicationContext, GoldRateWidget::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)

            for (widgetId in widgetIds) {
                GoldRateWidget.updateAppWidget(applicationContext, appWidgetManager, widgetId)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}