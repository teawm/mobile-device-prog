package com.example.insects.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.insects.InsectsApp
import com.example.insects.MainActivity
import com.example.insects.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GoldRateWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Первый виджет добавлен
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        // Последний виджет удалён
        super.onDisabled(context)
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_gold_rate)

            // Получаем репозиторий из Application
            val app = context.applicationContext as InsectsApp
            val repository = app.goldRepository

            // Запускаем корутину для получения данных
            CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
                try {
                    // Пытаемся обновить данные
                    repository.refreshGoldRate(force = false)
                } catch (e: Exception) {
                    // Ошибка сети — не критично
                }

                // Берём актуальный снимок
                val snapshot = repository.goldRateFlow.first()

                // Обновляем UI
                if (snapshot.hasData) {
                    val rubles = snapshot.priceInKopecks / 100
                    val kopecks = snapshot.priceInKopecks % 100
                    val priceText = "$rubles,${kopecks.toString().padStart(2, '0')}"

                    views.setTextViewText(R.id.textPrice, priceText)
                    views.setTextViewText(R.id.textUnit, "₽/г")
                } else {
                    views.setTextViewText(R.id.textPrice, "Нет\nданных")
                    views.setTextViewText(R.id.textUnit, "")
                }

                // Клик по виджету открывает приложение
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                // Обновляем виджет
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}