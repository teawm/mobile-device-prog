package com.example.insects

import android.widget.ImageView

class Bug(
    val imageView: ImageView,
    var currentX: Float,
    var currentY: Float,
    var targetX: Float,
    var targetY: Float,
    var speed: Long,
    var isAlive: Boolean = true
)