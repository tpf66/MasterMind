package com.simoni.name.mastermind.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    var date: Int,
    var version: Int,
    var result: String,
    var attempt: Int,
    var duration: Double
)