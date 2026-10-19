package com.example.insects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordsAdapter(
    private val onDeleteClick: (RecordDto) -> Unit
) : RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>() {

    private val records = mutableListOf<RecordDto>()

    fun submitList(newRecords: List<RecordDto>) {
        records.clear()
        records.addAll(newRecords)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount(): Int = records.size

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textPlayerName: TextView = itemView.findViewById(R.id.textPlayerName)
        private val textScore: TextView = itemView.findViewById(R.id.textScore)
        private val textDifficulty: TextView = itemView.findViewById(R.id.textDifficulty)
        private val textAccuracy: TextView = itemView.findViewById(R.id.textAccuracy)
        private val textDate: TextView = itemView.findViewById(R.id.textDate)

        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteRecord)

        fun bind(record: RecordDto) {
            textPlayerName.text = record.playerName
            textScore.text = "Очки: ${record.score}"
            textDifficulty.text = "Сложность: ${record.difficulty}"
            textAccuracy.text = "Точность: ${record.accuracy}"

            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            textDate.text = sdf.format(Date(record.timestamp))

            btnDelete.setOnClickListener {
                onDeleteClick(record)
            }
        }
    }
}