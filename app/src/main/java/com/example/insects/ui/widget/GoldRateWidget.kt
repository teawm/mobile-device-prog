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
import com.example.insects.data.repository.GoldRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GoldRateWidget : AppWidgetProvider(), KoinComponent {

    private val repository: GoldRepository by inject()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_gold_rate)
            val app = context.applicationContext as InsectsApp
            val repository = org.koin.core.context.GlobalContext.get().get<GoldRepository>()

            CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
                try {
                    repository.refreshGoldRate(force = false)
                } catch (e: Exception) { }

                val snapshot = repository.goldRateFlow.first()

                if (snapshot.hasData) {
                    val rubles = snapshot.priceInKopecks / 100
                    val kopecks = snapshot.priceInKopecks % 100
                    views.setTextViewText(R.id.textPrice, "$rubles,${kopecks.toString().padStart(2, '0')}")
                    views.setTextViewText(R.id.textUnit, "₽/г")
                } else {
                    views.setTextViewText(R.id.textPrice, "Нет\nданных")
                    views.setTextViewText(R.id.textUnit, "")
                }

                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}