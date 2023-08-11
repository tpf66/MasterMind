package com.simoni.name.mastermind.model

import androidx.compose.runtime.mutableStateOf
import java.util.Calendar

class InstantGame() {
    var secret = mutableStateOf("")
    var attempts = mutableStateOf("")
    var duration = mutableStateOf(0.0)
    var date = mutableStateOf("")

    val color = listOf("W","R","C","G","Y","P","O","B")
    /*
    W -> white
    R -> red
    C -> cyan
    G -> green
    Y -> yellow
    P -> purple
    O -> orange
    B -> brown
     */


    fun newMatch(){
        secret.value = color.random() + color.random() + color.random() + color.random() + color.random()
        attempts.value = ""
        duration.value = 0.0
        date.value = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString() + "/" +
                "" + (Calendar.getInstance().get(Calendar.MONTH)+1).toString() + "/" +
                "" + Calendar.getInstance().get(Calendar.YEAR).toString()
    }

    fun attempt(){
        //TODO
    }

    fun saveOnDb() {

    }

    fun loadMatch() {

    }

}