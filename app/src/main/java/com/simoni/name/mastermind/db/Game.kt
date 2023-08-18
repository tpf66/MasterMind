package com.simoni.name.mastermind.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simoni.name.mastermind.model.utils.Attempt

@Entity(tableName = "game_history")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val version: String, //Versione app
    val secretCode: String, //Codice segreto partita
    val result: String, //Risultato partita
    val attempts: Int, //# di tentativi
    val duration: Long, //Tempo impiegato
    val date: Long, //Data partita
    var isSelected: Boolean = false // per selezionare
)