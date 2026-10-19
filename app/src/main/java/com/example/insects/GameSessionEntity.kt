package com.example.insects

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["playerId"])]
)
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playerId: Int,
    val score: Int,
    val difficulty: String,
    val hits: Int,
    val misses: Int,
    val durationSeconds: Int,
    val timestamp: Long
)