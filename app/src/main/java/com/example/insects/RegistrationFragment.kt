package com.example.insects

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class RegistrationFragment : Fragment() {

    private var selectedDate: Calendar = Calendar.getInstance()
    private lateinit var prefs: SharedPreferences
    private lateinit var repository: GameRepository

    private lateinit var spinnerPlayers: Spinner
    private lateinit var btnRefreshPlayers: Button
    private var playersList = mutableListOf<PlayerEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)
        repository = GameRepository(requireContext())

        spinnerPlayers = view.findViewById(R.id.spinnerPlayers)
        btnRefreshPlayers = view.findViewById(R.id.btnRefreshPlayers)

        val editTextPlayerName = view.findViewById<EditText>(R.id.editTextPlayerName)
        val textCurrentPlayer = view.findViewById<TextView>(R.id.textCurrentPlayer)
        val editTextName = view.findViewById<EditText>(R.id.editTextName)
        val radioGroupGender = view.findViewById<RadioGroup>(R.id.radioGroupGender)
        val spinnerCourse = view.findViewById<Spinner>(R.id.spinnerCourse)
        val seekBarDifficulty = view.findViewById<SeekBar>(R.id.seekBarDifficulty)
        val textViewDifficultyValue = view.findViewById<TextView>(R.id.textViewDifficultyValue)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val imageViewZodiac = view.findViewById<ImageView>(R.id.imageViewZodiac)
        val buttonSubmit = view.findViewById<Button>(R.id.buttonSubmit)
        val textViewResult = view.findViewById<TextView>(R.id.textViewResult)

        calendarView.maxDate = System.currentTimeMillis()
        textViewDifficultyValue.text = (seekBarDifficulty.progress + 1).toString()

        seekBarDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewDifficultyValue.text = (progress + 1).toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            val formattedDate = "$dayOfMonth.${month + 1}.$year"
            Log.d("RegistrationFragment", "Выбрана дата: $formattedDate")
        }

        loadPlayersList()

        val savedPlayerName = prefs.getString("current_player_name", null)
        if (savedPlayerName != null) {
            textCurrentPlayer.text = "Текущий игрок: $savedPlayerName"
        }

        btnRefreshPlayers.setOnClickListener {
            loadPlayersList()
            Toast.makeText(requireContext(), "Список обновлён", Toast.LENGTH_SHORT).show()
        }

        spinnerPlayers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (playersList.isNotEmpty() && position > 0) { // position 0 = "Выберите игрока..."
                    val selectedPlayer = playersList[position - 1]
                    prefs.edit()
                        .putInt("current_player_id", selectedPlayer.id)
                        .putString("current_player_name", selectedPlayer.displayName)
                        .apply()
                    textCurrentPlayer.text = "Текущий игрок: ${selectedPlayer.displayName}"
                    Toast.makeText(requireContext(), "Выбран игрок: ${selectedPlayer.displayName}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        buttonSubmit.setOnClickListener {
            val playerName = editTextPlayerName.text.toString().trim()
            val name = editTextName.text.toString().trim()

            if (playerName.isEmpty()) {
                Toast.makeText(requireContext(), "Введите имя игрока для рекордов!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Пожалуйста, введите ФИО", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val normalizedName = playerName.lowercase()

            viewLifecycleOwner.lifecycleScope.launch {
                var player = repository.getPlayerByName(normalizedName)

                if (player == null) {
                    val newPlayer = PlayerEntity(name = normalizedName, displayName = playerName)
                    val newId = repository.insertPlayer(newPlayer)
                    player = PlayerEntity(id = newId.toInt(), name = normalizedName, displayName = playerName)
                    Toast.makeText(requireContext(), "Новый игрок создан!", Toast.LENGTH_SHORT).show()

                    loadPlayersList()
                }

                val gender = when (radioGroupGender.checkedRadioButtonId) {
                    R.id.radioMale -> "Мужской"
                    R.id.radioFemale -> "Женский"
                    else -> "Не указан"
                }
                val course = spinnerCourse.selectedItem.toString()
                val difficulty = seekBarDifficulty.progress + 1
                val day = selectedDate.get(Calendar.DAY_OF_MONTH)
                val month = selectedDate.get(Calendar.MONTH) + 1
                val year = selectedDate.get(Calendar.YEAR)

                Log.d("RegistrationFragment", "Дата рождения: $day.$month.$year")

                val zodiacSign = getZodiacSign(day, month)
                Log.d("RegistrationFragment", "Знак зодиака: $zodiacSign")

                imageViewZodiac.setImageResource(getZodiacImageResource(zodiacSign))

                prefs.edit()
                    .putInt("current_player_id", player.id)
                    .putString("current_player_name", playerName)
                    .putInt("key_difficulty", difficulty)
                    .apply()

                textCurrentPlayer.text = "Текущий игрок: $playerName"

                val resultText = """
                    Игрок: $playerName
                    ФИО: $name
                    Пол: $gender
                    Курс: $course
                    Сложность: $difficulty
                    Дата рождения: $day.$month.$year
                    Знак зодиака: $zodiacSign
                """.trimIndent()

                textViewResult.text = resultText
            }
        }
    }

    private fun loadPlayersList() {
        viewLifecycleOwner.lifecycleScope.launch {
            playersList.clear()
            playersList.addAll(repository.getAllPlayersList())

            val playerNames = mutableListOf("Выберите игрока...")
            playerNames.addAll(playersList.map { it.displayName })

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, playerNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPlayers.adapter = adapter
        }
    }

    private fun getZodiacSign(day: Int, month: Int): String {
        return when {
            (month == 12 && day >= 22) || (month == 1 && day <= 19) -> "Козерог"
            (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "Водолей"
            (month == 2 && day >= 19) || (month == 3 && day <= 20) -> "Рыбы"
            (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "Овен"
            (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "Телец"
            (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "Близнецы"
            (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "Рак"
            (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "Лев"
            (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "Дева"
            (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "Весы"
            (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "Скорпион"
            (month == 11 && day >= 22) || (month == 12 && day <= 21) -> "Стрелец"
            else -> "Неизвестно"
        }
    }

    private fun getZodiacImageResource(zodiacSign: String): Int {
        return when (zodiacSign) {
            "Козерог" -> R.drawable.zodiac_capricorn
            "Водолей" -> R.drawable.zodiac_aquarius
            "Рыбы" -> R.drawable.zodiac_pisces
            "Овен" -> R.drawable.zodiac_aries
            "Телец" -> R.drawable.zodiac_taurus
            "Близнецы" -> R.drawable.zodiac_gemini
            "Рак" -> R.drawable.zodiac_cancer
            "Лев" -> R.drawable.zodiac_leo
            "Дева" -> R.drawable.zodiac_virgo
            "Весы" -> R.drawable.zodiac_libra
            "Скорпион" -> R.drawable.zodiac_scorpio
            "Стрелец" -> R.drawable.zodiac_sagittarius
            else -> android.R.drawable.ic_menu_help
        }
    }
}