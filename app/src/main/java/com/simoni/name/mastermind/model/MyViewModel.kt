package com.simoni.name.mastermind.model

import com.simoni.name.mastermind.model.MyState.*
import androidx.compose.runtime.mutableStateOf
import com.simoni.name.mastermind.db.Repository

class MyViewModel(instantGame: InstantGame, repository: Repository) {
    var instanGame = instantGame
    var state = mutableStateOf(Init)
    var n = 0

    fun playHome() {
        state.value = Init
        instanGame.newMatch()

    }

    fun historyHome() {

    }




    //Funzioni di prova
    fun init(){
        state.value = Init
        instanGame.newMatch()
    }
    fun new(){
        state.value = NewGame
    }

    fun hi(){
        state.value = History
    }
}