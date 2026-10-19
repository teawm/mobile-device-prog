package com.example.insects

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).gameDao()

    fun getAllPlayers(): Flow<List<PlayerEntity>> {
        throw NotImplementedError("Используем suspend getPlayerByName и insertPlayer")
    }

    suspend fun getAllPlayersList(): List<PlayerEntity> = withContext(Dispatchers.IO) {
        dao.getAllPlayersList()
    }

    suspend fun getPlayerByName(normalizedName: String): PlayerEntity? = withContext(Dispatchers.IO) {
        dao.getPlayerByName(normalizedName)
    }

    suspend fun insertPlayer(player: PlayerEntity): Long = withContext(Dispatchers.IO) {
        dao.insertPlayer(player)
    }

    suspend fun insertSession(session: GameSessionEntity) = withContext(Dispatchers.IO) {
        dao.insertSession(session)
    }

    suspend fun deleteSession(sessionId: Int) = withContext(Dispatchers.IO) {
        dao.deleteSession(sessionId)
    }

    fun getRecords(playerId: Int? = null): Flow<List<RecordDto>> {
        return dao.getRecords(playerId)
    }
}