package com.example.insects

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.code
    }

    @TypeConverter
    fun toDifficulty(code: String): Difficulty {
        return Difficulty.fromCode(code)
    }
}