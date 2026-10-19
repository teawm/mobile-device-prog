package com.example.insects

enum class Difficulty(val code: String) {
    LEVEL_1("LEVEL_1"),
    LEVEL_2("LEVEL_2"),
    LEVEL_3("LEVEL_3"),
    LEVEL_4("LEVEL_4"),
    LEVEL_5("LEVEL_5");

    companion object {
        fun fromCode(code: String): Difficulty {
            return entries.find { it.code == code } ?: LEVEL_1
        }
    }
}