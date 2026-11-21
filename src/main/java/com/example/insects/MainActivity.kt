package com.example.insects

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position != 0) {
                    adapter.gameFragment?.pauseGame()
                }
            }
        })

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Игра"
                1 -> tab.text = "Регистрация"
                2 -> tab.text = "Правила"
                3 -> tab.text = "Авторы"
                4 -> tab.text = "Настройки"
                5 -> tab.text = "Рекорды"
            }
        }.attach()

        // Запускаем первичное обновление курса золота
        lifecycleScope.launch {
            val app = application as InsectsApp
            app.goldRepository.refreshGoldRate(force = false)
        }

        // Настраиваем периодическое обновление виджета (раз в 4 часа)
        val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.insects.ui.widget.GoldRateUpdateWorker>(
            4, java.util.concurrent.TimeUnit.HOURS
        )
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build()
            )
            .build()

        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "gold_rate_update",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}