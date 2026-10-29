package com.example.insects

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).gameDao()

    suspend fun getAllPlayersList(): List<PlayerEntity> = withContext(Dispatchers.IO) {
        dao.getAllPlayersList()
    }

    suspend fun getPlayerByName(normalizedName: String): PlayerEntity? =
        withContext(Dispatchers.IO) {
            dao.getPlayerByName(normalizedName)
        }

    suspend fun insertPlayer(player: PlayerEntity): Long = withContext(Dispatchers.IO) {
        dao.insertPlayer(player)
    }

    suspend fun getSessionByPlayerAndDifficulty(playerId: Int, difficulty: String): GameSessionEntity? = withContext(Dispatchers.IO) {
        dao.getSessionByPlayerAndDifficulty(playerId, difficulty)
    }

    suspend fun insertOrUpdateSession(session: GameSessionEntity) = withContext(Dispatchers.IO) {
        val existingSession =
            dao.getSessionByPlayerAndDifficulty(session.playerId, session.difficulty)

        if (existingSession == null) {
            dao.insertSession(session)
        } else if (session.score > existingSession.score) {
            dao.updateSession(session.copy(id = existingSession.id))
        }
    }

    suspend fun deleteSession(sessionId: Int) = withContext(Dispatchers.IO) {
        dao.deleteSession(sessionId)
    }

    fun getRecords(playerId: Int? = null): Flow<List<RecordDto>> {
        return dao.getRecords(playerId)
    }
}