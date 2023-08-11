package com.simoni.name.mastermind.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface DaoGame {
    @Insert
    fun insert(game: Game)

    @Delete
    fun delete(game: Game)

    @Query(""" SELECT * FROM game ORDER BY date DESC """)
    fun selectAll(): Game?
}
