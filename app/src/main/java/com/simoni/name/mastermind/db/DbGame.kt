package com.simoni.name.mastermind.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [Game::class], version = 1)
abstract class DbGame: RoomDatabase() {
    abstract fun gameDao(): DaoGame

    companion object{
        private var db: DbGame? = null

        fun getInstance(context: Context): DbGame{
                db = databaseBuilder(context, DbGame::class.java, "game.db").build()
            return db as DbGame
        }
    }
}