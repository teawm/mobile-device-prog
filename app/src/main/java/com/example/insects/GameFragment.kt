package com.example.insects

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.SoundPool
import android.view.animation.LinearInterpolator
import kotlin.math.abs
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

class GameFragment : Fragment(), SensorEventListener {

    private lateinit var textScore: TextView
    private lateinit var textTime: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var gameField: FrameLayout

    private var score = 0
    private var timeLeft = 60
    private var gameJob: Job? = null
    private var timerJob: Job? = null
    private var bonusJob: Job? = null
    private lateinit var prefs: SharedPreferences
    private lateinit var repository: GameRepository

    private var hits = 0
    private var misses = 0
    private var gameStartedAt: Long = 0

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var soundPool: SoundPool? = null
    private var bugSoundId: Int = 0

    private var isBonusActive = false
    private var currentBonusView: ImageView? = null
    private var gravityX = 0f
    private var gravityY = 0f


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

        prefs = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)
        repository = GameRepository(requireContext())

        textScore = view.findViewById(R.id.textScore)
        textTime = view.findViewById(R.id.textTime)
        btnStart = view.findViewById(R.id.btnStart)
        btnStop = view.findViewById(R.id.btnStop)
        gameField = view.findViewById(R.id.gameField)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        soundPool = SoundPool.Builder().setMaxStreams(1).build()
        bugSoundId = soundPool?.load(requireContext(), R.raw.insect_sound, 1) ?: 0

        btnStart.setOnClickListener { startGame() }
        btnStop.setOnClickListener { stopGame() }

        gameField.setOnClickListener {
            if (gameJob?.isActive == true) {
                misses++
                addScore(-1)
            }
        }
    }

    private fun startGame() {
        val playerId = prefs.getInt("current_player_id", -1)
        if (playerId == -1) {
            Toast.makeText(requireContext(), "Сначала зарегистрируйте игрока!", Toast.LENGTH_LONG).show()
            return
        }

        val duration = prefs.getInt("key_duration", 60)
        val speed = prefs.getInt("key_speed", 5)
        val maxBugs = prefs.getInt("key_cockroaches", 10)
        val spawnDelay = (2000 - (speed * 150)).toLong()

        score = 0
        timeLeft = duration
        hits = 0
        misses = 0
        gameStartedAt = System.currentTimeMillis()
        isBonusActive = false
        gravityX = 0f
        gravityY = 0f

        updateUI()
        clearField()

        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        gameJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                if (gameField.childCount < maxBugs) {
                    spawnBug()
                }
                delay(spawnDelay)
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

        bonusJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(15000)
            while (isActive) {
                spawnBonus()
                delay(15000)
            }
        }
    }

    private fun stopGame() {
        gameJob?.cancel()
        timerJob?.cancel()
        bonusJob?.cancel()
        clearField()

        sensorManager?.unregisterListener(this)
        isBonusActive = false

        val playerId = prefs.getInt("current_player_id", -1)
        if (playerId != -1) {
            viewLifecycleOwner.lifecycleScope.launch {
                val difficultyLevel = prefs.getInt("key_difficulty", 3)
                val duration = ((System.currentTimeMillis() - gameStartedAt) / 1000).toInt()

                val session = GameSessionEntity(
                    playerId = playerId,
                    score = score,
                    difficulty = "LEVEL_$difficultyLevel",
                    hits = hits,
                    misses = misses,
                    durationSeconds = duration,
                    timestamp = System.currentTimeMillis()
                )

                val existingSession = repository.getSessionByPlayerAndDifficulty(playerId, session.difficulty)
                val isNewRecord = existingSession == null || session.score > existingSession.score

                repository.insertOrUpdateSession(session)

                val message = if (isNewRecord) {
                    "Новый рекорд! Счет: $score"
                } else {
                    "Результат сохранен! Счет: $score"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "Игра окончена! Счет: $score", Toast.LENGTH_LONG).show()
        }
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
        for (i in 0 until gameField.childCount) {
            val child = gameField.getChildAt(i)
            child.animate().cancel()
            child.clearAnimation()
        }
        gameField.removeAllViews()
        currentBonusView = null
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
                hits++
                addScore(10)
                gameField.removeView(it)
            }
        }

        gameField.addView(bugImage)
        moveBug(bugImage, bugSize)
    }

    private fun moveBug(bugImage: ImageView, bugSize: Int) {
        if (bugImage.parent == null) return

        val fieldWidth = gameField.width
        val fieldHeight = gameField.height

        val targetX: Float
        val targetY: Float
        val duration: Long
        val interpolator: android.view.animation.Interpolator

        if (isBonusActive) {
            val speedMultiplier = 300f
            targetX = (bugImage.x + gravityX * speedMultiplier).coerceIn(0f, (fieldWidth - bugSize).toFloat())
            targetY = (bugImage.y + gravityY * speedMultiplier).coerceIn(0f, (fieldHeight - bugSize).toFloat())

            duration = 100L
            interpolator = LinearInterpolator()
        } else {
            targetX = Random.nextFloat() * (fieldWidth - bugSize)
            targetY = Random.nextFloat() * (fieldHeight - bugSize)
            duration = 2000L
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }

        val deltaX = targetX - bugImage.x
        val deltaY = targetY - bugImage.y

        if (abs(deltaX) > 1f || abs(deltaY) > 1f) {
            val angle = Math.toDegrees(Math.atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
            bugImage.rotation = angle + 90f
        }

        bugImage.animate().cancel()

        bugImage.animate()
            .x(targetX)
            .y(targetY)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .withEndAction {
                moveBug(bugImage, bugSize)
            }
            .start()
    }

    private fun spawnBonus() {
        if (gameJob?.isActive != true) return
        if (currentBonusView != null) return

        val bonusImage = ImageView(requireContext())
        bonusImage.setImageResource(R.drawable.bugbonus)

        val bonusSize = 120
        val layoutParams = FrameLayout.LayoutParams(bonusSize, bonusSize)
        bonusImage.layoutParams = layoutParams

        val startX = Random.nextFloat() * (gameField.width - bonusSize)
        val startY = Random.nextFloat() * (gameField.height - bonusSize)

        bonusImage.x = startX
        bonusImage.y = startY

        bonusImage.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(500)
            .withEndAction {
                bonusImage.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .start()
            }
            .start()

        bonusImage.setOnClickListener {
            activateBonus()
            gameField.removeView(it)
            currentBonusView = null
        }

        gameField.addView(bonusImage)
        currentBonusView = bonusImage

        viewLifecycleOwner.lifecycleScope.launch {
            delay(5000)
            if (bonusImage.parent != null) {
                gameField.removeView(bonusImage)
                if (currentBonusView == bonusImage) {
                    currentBonusView = null
                }
            }
        }
    }

    private fun activateBonus() {
        isBonusActive = true

        soundPool?.play(bugSoundId, 1f, 1f, 1, 0, 1f)

        Toast.makeText(requireContext(), "Бонус активирован! Жуки падают!", Toast.LENGTH_SHORT).show()

        applyGravityToAllBugs()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(5000)
            isBonusActive = false
            if (gameJob?.isActive == true) {
                Toast.makeText(requireContext(), "Бонус закончился", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyGravityToAllBugs() {
        for (i in 0 until gameField.childCount) {
            val child = gameField.getChildAt(i)
            if (child is ImageView && child != currentBonusView) {
                child.animate().cancel()
                moveBug(child, child.width)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            gravityX = -(event.values[0] / SensorManager.GRAVITY_EARTH).coerceIn(-1f, 1f)
            gravityY = (event.values[1] / SensorManager.GRAVITY_EARTH).coerceIn(-1f, 1f)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroyView() {
        super.onDestroyView()
        stopGame()
        soundPool?.release()
        soundPool = null
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (gameJob?.isActive == true) {
            accelerometer?.let {
                sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    fun pauseGame() {
        if (gameJob?.isActive == true || timerJob?.isActive == true) {
            stopGame()
        }
    }
}