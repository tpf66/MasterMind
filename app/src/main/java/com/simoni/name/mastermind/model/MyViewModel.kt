package com.simoni.name.mastermind.model

import com.simoni.name.mastermind.db.Game
import com.simoni.name.mastermind.db.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyViewModel(inGame: InstantGame, repo: Repository) {
    val instantGame : InstantGame
    val repository : Repository
    //var state = mutableStateOf(Init)
    var n = 0

    init {
        instantGame = inGame
        repository = repo
    }

    suspend fun getAllGameHistory(): List<Game> {
        return withContext(Dispatchers.IO) {
            repository.readAll()
        }
    }

    suspend fun deleteSelectedGames(selectedGames: List<Game>) {
        for (game in selectedGames) {
            repository.deleteGameHistory(game)
        }
    }
}


// // Home Function
// fun playHome() {
// state.value = Match
// }
//
// fun continueHome() {
// state.value = Match
// // visualizza la partita interrotta
// }
//
// fun historyHome() {
// state.value = History
// // va nella schermata della history
// }
//
//
// // Match function
// fun guessMatch() {
// // implementa il tentativo e il gioco,
// // controlla se il giocatore ha vinto o no
// }
//
// fun backMatch() {
// state.value = Init
// // torna alla schermata iniziale, tenendo in memoria la partita
// }
//
// fun closeMatch() {
// // chiude la partita e salva su db
// }
//
//
// // History function
// fun deleteGameHistory() {
// // elimina una entry dal db
// }
//
// fun backHistory() {
// state.value = Init
// // torna alla schermata home
// }
//
// fun loadGameHistory() {
// // se ci va carica una partita lasciata a meta
// }