package com.simoni.name.mastermind.db

import androidx.room.*

@Dao
interface MastermindDao {
    @Insert
    suspend fun insertGameHistory(gameHistory: Game)

    @Delete
    suspend fun deleteGameHistory(game: Game)

    @Query("DELETE FROM game_history")
    suspend fun deleteAllGameHistory()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameHistoryList(gameHistoryList: List<Game>)

    @Query("SELECT * FROM game_history")
    fun getAllGameHistory(): List<Game>

    @Query("SELECT MAX(id) + 1 FROM game_history")
    suspend fun getNextId(): Long
}