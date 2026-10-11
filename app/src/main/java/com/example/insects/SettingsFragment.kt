package com.example.insects
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private lateinit var prefs: SharedPreferences

    private val KEY_SPEED = "key_speed"
    private val KEY_COCKROACHES = "key_cockroaches"
    private val KEY_DURATION = "key_duration"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)

        val seekBarSpeed = view.findViewById<SeekBar>(R.id.seekBarSpeed)
        val seekBarCockroaches = view.findViewById<SeekBar>(R.id.seekBarMaxCockroaches)
        val seekBarDuration = view.findViewById<SeekBar>(R.id.seekBarRoundDuration)

        val textSpeed = view.findViewById<TextView>(R.id.textSpeedValue)
        val textCockroaches = view.findViewById<TextView>(R.id.textCockroachesValue)
        val textDuration = view.findViewById<TextView>(R.id.textDurationValue)

        val buttonSave = view.findViewById<Button>(R.id.buttonSaveSettings)

        seekBarSpeed.progress = prefs.getInt(KEY_SPEED, 5)
        seekBarCockroaches.progress = prefs.getInt(KEY_COCKROACHES, 10)
        seekBarDuration.progress = prefs.getInt(KEY_DURATION, 60)

        textSpeed.text = seekBarSpeed.progress.toString()
        textCockroaches.text = seekBarCockroaches.progress.toString()
        textDuration.text = seekBarDuration.progress.toString()

        seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textSpeed.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarCockroaches.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textCockroaches.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textDuration.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        buttonSave.setOnClickListener {
            prefs.edit()
                .putInt(KEY_SPEED, seekBarSpeed.progress)
                .putInt(KEY_COCKROACHES, seekBarCockroaches.progress)
                .putInt(KEY_DURATION, seekBarDuration.progress)
                .apply()

            Toast.makeText(requireContext(), "Настройки сохранены!", Toast.LENGTH_SHORT).show()
        }
    }
}