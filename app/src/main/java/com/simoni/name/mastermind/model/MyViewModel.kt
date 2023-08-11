package com.simoni.name.mastermind.model

import com.simoni.name.mastermind.model.MyState.*
import androidx.compose.runtime.mutableStateOf

class MyViewModel(instantGame: IstantGame) {
    var state = mutableStateOf(Init)
    var n = 0






    fun init(){
        state.value = Init
    }
    fun new(){
        state.value = NewGame
    }

    fun hi(){
        state.value = History
    }
}