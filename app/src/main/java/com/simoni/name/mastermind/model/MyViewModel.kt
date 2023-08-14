package com.simoni.name.mastermind.model

import com.simoni.name.mastermind.model.MyState.*
import androidx.compose.runtime.mutableStateOf
import com.simoni.name.mastermind.db.Repository

class MyViewModel(instantGame: InstantGame, repository: Repository) {
    var instanGame = instantGame
    var state = mutableStateOf(Init)
    var n = 0


    // Home Function
    fun playHome() {
        state.value = Match
        instanGame.newMatch()
    }

    fun continueHome() {
        state.value = Match
        // visualizza la partita interrotta
    }

    fun historyHome() {
        state.value = History
        // va nella schermata della history
    }


    // Match function
    fun guessMatch() {
        // implementa il tentativo e il gioco,
        // controlla se il giocatore ha vinto o no
    }

    fun backMatch() {
        state.value = Init
        // torna alla schermata iniziale, tenendo in memoria la partita
    }

    fun closeMatch() {
        // chiude la partita e salva su db
    }


    // History function
    fun deleteGameHistory() {
        // elimina una entry dal db
    }

    fun backHistory() {
        state.value = Init
        // torna alla schermata home
    }

    fun loadGameHistory() {
        // se ci va carica una partita lasciata a meta
    }
}