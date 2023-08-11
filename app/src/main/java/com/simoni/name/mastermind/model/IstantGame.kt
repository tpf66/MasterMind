package com.simoni.name.mastermind.model

import android.content.Context
import androidx.compose.runtime.mutableStateOf

class IstantGame(context: Context) {
    var secret = mutableStateOf("")
    var attempts = mutableStateOf("")
    var duration = mutableStateOf(0.0)
    var date = mutableStateOf(0)


    fun init(){
        secret.value = "RBGYP"
    }

}