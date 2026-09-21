package com.example.insects

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val radioGroupGender = findViewById<RadioGroup>(R.id.radioGroupGender)
        val spinnerCourse = findViewById<Spinner>(R.id.spinnerCourse)
        val seekBarDifficulty = findViewById<SeekBar>(R.id.seekBarDifficulty)
        val textViewDifficultyValue = findViewById<TextView>(R.id.textViewDifficultyValue)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val imageViewZodiac = findViewById<ImageView>(R.id.imageViewZodiac)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)
        val textViewResult = findViewById<TextView>(R.id.textViewResult)
        textViewDifficultyValue.text = seekBarDifficulty.progress.toString()

        seekBarDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewDifficultyValue.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
        }

        buttonSubmit.setOnClickListener {
            val name = editTextName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите ФИО", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gender = when (radioGroupGender.checkedRadioButtonId) {
                R.id.radioMale -> "Мужской"
                R.id.radioFemale -> "Женский"
                else -> "Не указан"
            }

            val course = spinnerCourse.selectedItem.toString()

            val difficulty = seekBarDifficulty.progress

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = calendarView.date

            val day = selectedDate.get(Calendar.DAY_OF_MONTH)
            val month = selectedDate.get(Calendar.MONTH) + 1
            val year = selectedDate.get(Calendar.YEAR)

            val zodiacSign = getZodiacSign(day, month)

            imageViewZodiac.setImageResource(getZodiacImageResource(zodiacSign))

            val resultText = """
                ФИО: $name
                Пол: $gender
                Курс: $course
                Уровень сложности: $difficulty
                Дата рождения: $day.$month.$year
                Знак зодиака: $zodiacSign
            """.trimIndent()

            textViewResult.text = resultText
        }
    }

    private fun getZodiacSign(day: Int, month: Int): String {
        return when {
            // Козерог: 22.12 - 19.01
            (month == 12 && day >= 22) || (month == 1 && day <= 19) -> "Козерог"
            // Водолей: 20.01 - 18.02
            (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "Водолей"
            // Рыбы: 19.02 - 20.03
            (month == 2 && day >= 19) || (month == 3 && day <= 20) -> "Рыбы"
            // Овен: 21.03 - 19.04
            (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "Овен"
            // Телец: 20.04 - 20.05
            (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "Телец"
            // Близнецы: 21.05 - 20.06
            (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "Близнецы"
            // Рак: 21.06 - 22.07
            (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "Рак"
            // Лев: 23.07 - 22.08
            (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "Лев"
            // Дева: 23.08 - 22.09
            (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "Дева"
            // Весы: 23.09 - 22.10
            (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "Весы"
            // Скорпион: 23.10 - 21.11
            (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "Скорпион"
            // Стрелец: 22.11 - 21.12
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