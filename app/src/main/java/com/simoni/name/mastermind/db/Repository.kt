package com.simoni.name.mastermind.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// function of the db
class Repository(private val dao: MastermindDao) {
    suspend fun readAll(): List<Game> {
        return withContext(Dispatchers.IO) {
            dao.getAllGameHistory()
        }
    }

    suspend fun insert(game: Game) {
        withContext(Dispatchers.IO) {
            dao.insertGameHistory(game)
        }
    }
    suspend fun getNextId(): Long? {
        return dao.getNextId()
    }
    suspend fun deleteGameHistory(game: Game) {
        withContext(Dispatchers.IO) {
            dao.deleteGameHistory(game)
        }
    }
}