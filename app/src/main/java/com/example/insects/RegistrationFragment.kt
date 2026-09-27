package com.example.insects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.util.Calendar

class RegistrationFragment : Fragment() {

    private var selectedDate: Calendar = Calendar.getInstance()

    // XML
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // создание View из XML-файла
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    // работа с элементами
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        textViewDifficultyValue.text = seekBarDifficulty.progress.toString()

        seekBarDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewDifficultyValue.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            val formattedDate = "$dayOfMonth.${month + 1}.$year"
            Log.d("RegistrationFragment", "Выбрана дата: $formattedDate")
        }

        //  "Сохранить и показать"
        buttonSubmit.setOnClickListener {
            val name = editTextName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Пожалуйста, введите ФИО", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gender = when (radioGroupGender.checkedRadioButtonId) {
                R.id.radioMale -> "Мужской"
                R.id.radioFemale -> "Женский"
                else -> "Не указан"
            }

            val course = spinnerCourse.selectedItem.toString()

            val difficulty = seekBarDifficulty.progress

            val day = selectedDate.get(Calendar.DAY_OF_MONTH)
            val month = selectedDate.get(Calendar.MONTH) + 1
            val year = selectedDate.get(Calendar.YEAR)

            Log.d("RegistrationFragment", "Дата рождения: $day.$month.$year")

            val zodiacSign = getZodiacSign(day, month)
            Log.d("RegistrationFragment", "Знак зодиака: $zodiacSign")

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