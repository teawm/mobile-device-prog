package com.example.insects

data class RecordDto(
    val sessionId: Int,
    val playerName: String,
    val score: Int,
    val difficulty: String,
    val hits: Int,
    val misses: Int,
    val timestamp: Long
) {
    val accuracy: String
        get() {
            val total = hits + misses
            return if (total == 0) "нет данных"
            else "${(hits * 100 / total)}%"
        }
}