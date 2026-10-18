package com.example.insects

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Insert
    suspend fun insertSession(session: GameSessionEntity)

    @Query("SELECT * FROM players WHERE name = :normalizedName LIMIT 1")
    suspend fun getPlayerByName(normalizedName: String): PlayerEntity?

    @Query("SELECT * FROM players ORDER BY name ASC")
    suspend fun getAllPlayersList(): List<PlayerEntity>

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Int)

    @Query("""
        SELECT s.id as sessionId, p.displayName as playerName, s.score, s.difficulty, s.hits, s.misses, s.timestamp 
        FROM sessions s 
        INNER JOIN players p ON s.playerId = p.id 
        WHERE (:playerId IS NULL OR s.playerId = :playerId)
        ORDER BY s.score DESC, s.timestamp DESC, s.id DESC
    """)
    fun getRecords(playerId: Int? = null): Flow<List<RecordDto>>
}