package com.example.insects

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        // Устанавливаем адаптер
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Связываем вкладки с фрагментами и задаем им названия
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Регистрация"
                1 -> tab.text = "Правила"
                2 -> tab.text = "Авторы"
                3 -> tab.text = "Настройки"
            }
        }.attach()
    }
}