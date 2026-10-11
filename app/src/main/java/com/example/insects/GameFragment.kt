package com.example.insects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameFragment : Fragment() {

    private lateinit var textScore: TextView
    private lateinit var textTime: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var gameField: FrameLayout

    private var score = 0
    private var timeLeft = 60
    private var gameJob: Job? = null
    private var timerJob: Job? = null

    private val bugImages = intArrayOf(
        R.drawable.bug1,
        R.drawable.bug2,
        R.drawable.bug3
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textScore = view.findViewById(R.id.textScore)
        textTime = view.findViewById(R.id.textTime)
        btnStart = view.findViewById(R.id.btnStart)
        btnStop = view.findViewById(R.id.btnStop)
        gameField = view.findViewById(R.id.gameField)

        btnStart.setOnClickListener { startGame() }
        btnStop.setOnClickListener { stopGame() }

        gameField.setOnClickListener {
            if (gameJob?.isActive == true) {
                addScore(-1)
            }
        }
    }

    private fun startGame() {
        score = 0
        timeLeft = 60
        updateUI()
        clearField()

        gameJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                spawnBug()
                delay(1000)
            }
        }

        timerJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive && timeLeft > 0) {
                delay(1000)
                timeLeft--
                updateUI()
            }
            if (timeLeft <= 0) stopGame()
        }
    }

    private fun stopGame() {
        gameJob?.cancel()
        timerJob?.cancel()
        clearField()
        Toast.makeText(requireContext(), "Игра остановлена. Счет: $score", Toast.LENGTH_SHORT).show()
    }

    private fun addScore(points: Int) {
        score += points
        updateUI()
    }

    private fun updateUI() {
        textScore.text = "Очки: $score"
        textTime.text = "Время: $timeLeft"
    }

    private fun clearField() {
        gameField.removeAllViews()
    }

    private fun spawnBug() {
        val bugImage = ImageView(requireContext())

        val randomImageIndex = Random.nextInt(bugImages.size)
        bugImage.setImageResource(bugImages[randomImageIndex])

        val bugSize = 150
        val layoutParams = FrameLayout.LayoutParams(bugSize, bugSize)
        bugImage.layoutParams = layoutParams

        val startX = Random.nextFloat() * (gameField.width - bugSize)
        val startY = Random.nextFloat() * (gameField.height - bugSize)

        bugImage.x = startX
        bugImage.y = startY

        bugImage.setOnClickListener {
            if (gameJob?.isActive == true) {
                addScore(10)
                gameField.removeView(it)
            }
        }

        gameField.addView(bugImage)

        moveBug(bugImage, bugSize)
    }

    private fun moveBug(bugImage: ImageView, bugSize: Int) {
        if (bugImage.parent == null) return

        val newX = Random.nextFloat() * (gameField.width - bugSize)
        val newY = Random.nextFloat() * (gameField.height - bugSize)

        val currentX = bugImage.x
        val currentY = bugImage.y
        val deltaX = newX - currentX
        val deltaY = newY - currentY
        val angle = Math.toDegrees(Math.atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()

        val rotationCorrection = 90f
        bugImage.rotation = angle + rotationCorrection

        bugImage.animate()
            .x(newX)
            .y(newY)
            .setDuration(2000)
            .withEndAction {
                moveBug(bugImage, bugSize)
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopGame()
    }
}