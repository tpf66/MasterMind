package com.simoni.name.mastermind.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simoni.name.mastermind.model.utils.Difficulty
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "game_history")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val version: String, // App version
    val secretCode: String, // secret code
    val result: String, // game result
    val stratt: String, // list of attempts
    val numatt: Int, // # of attempts
    val duration: Long, // duration
    val date: Long, // date
    var difficulty: Difficulty // game mode
) : Parcelable