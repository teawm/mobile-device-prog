package com.example.insects

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordsFragment : Fragment() {

    private lateinit var tableRecords: TableLayout
    private lateinit var repository: GameRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = GameRepository(requireContext())
        tableRecords = view.findViewById(R.id.tableRecords)

        viewLifecycleOwner.lifecycleScope.launch {
            repository.getRecords().collectLatest { records ->
                updateTable(records)
            }
        }
    }

    private fun updateTable(records: List<RecordDto>) {
        while (tableRecords.childCount > 1) {
            tableRecords.removeViewAt(tableRecords.childCount - 1)
        }

        records.forEachIndexed { index, record ->
            val row = createTableRow(record, index)
            tableRecords.addView(row)
        }
    }

    private fun createTableRow(record: RecordDto, position: Int): TableRow {
        val row = TableRow(requireContext())

        val backgroundColor = if (position % 2 == 0) Color.WHITE else Color.parseColor("#F5F5F5")
        row.setBackgroundColor(backgroundColor)
        row.setPadding(8, 8, 8, 8)

        val textPlayerName = TextView(requireContext()).apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            text = record.playerName
            textSize = 14f
            gravity = android.view.Gravity.CENTER
        }

        val textScore = TextView(requireContext()).apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            text = record.score.toString()
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            setTextColor(Color.parseColor("#00796B"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val textDifficulty = TextView(requireContext()).apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            text = record.difficulty
            textSize = 12f
            gravity = android.view.Gravity.CENTER
        }

        val textAccuracy = TextView(requireContext()).apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            text = record.accuracy
            textSize = 12f
            gravity = android.view.Gravity.CENTER
        }

        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val textDate = TextView(requireContext()).apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            text = sdf.format(Date(record.timestamp))
            textSize = 11f
            gravity = android.view.Gravity.CENTER
            setTextColor(Color.parseColor("#888888"))
        }

        val btnDelete = ImageButton(requireContext()).apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            setImageResource(android.R.drawable.ic_menu_delete)
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = "Удалить запись"
            setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.deleteSession(record.sessionId)
                }
            }
        }

        row.addView(textPlayerName)
        row.addView(textScore)
        row.addView(textDifficulty)
        row.addView(textAccuracy)
        row.addView(textDate)
        row.addView(btnDelete)

        return row
    }
}