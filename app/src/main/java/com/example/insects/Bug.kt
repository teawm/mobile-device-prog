package com.example.insects

import android.widget.ImageView

class Bug(
    val imageView: ImageView,       // Визуальное представление жука
    var currentX: Float,            // Текущая координата X
    var currentY: Float,            // Текущая координата Y
    var targetX: Float,             // Целевая координата X (куда ползёт)
    var targetY: Float,             // Целевая координата Y
    var speed: Long,                // Скорость (время анимации в мс)
    var isAlive: Boolean = true     // Жив ли жук (не уничтожен ли)
)