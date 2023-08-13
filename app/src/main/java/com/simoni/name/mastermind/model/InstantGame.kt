package com.simoni.name.mastermind.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.util.Calendar


class InstantGame() {
    var secret = mutableStateOf("")
    var attempts = mutableStateListOf("")
    var duration = mutableStateOf(System.currentTimeMillis())
    var date = mutableStateOf("")
    var status = mutableStateOf("")
    var life = mutableStateOf(0)
    val color = listOf("W", "R", "C", "G", "Y", "P", "O", "B")
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


    fun newMatch() {
        secret.value =
            color.random() + color.random() + color.random() + color.random() + color.random()
        attempts.clear()
        duration.value = System.currentTimeMillis()
        duration.value.javaClass.kotlin.simpleName
        date.value = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString() + "/" +
                "" + (Calendar.getInstance().get(Calendar.MONTH) + 1).toString() + "/" +
                "" + Calendar.getInstance().get(Calendar.YEAR).toString()
        status.value = "ongoing"
        life.value = 10
    }


    fun attempt(guess: String) {
        var ngg: Int = 0
        var ngs: Int = 0
        var newSecret = ""
        var newGuess = ""
        val evaluatedChars = mutableListOf<Char>()

        // Numero di cifre giuste al posto giusto
        for(i in 0 until secret.value.length)
            if(secret.value[i] == guess[i])
                ngg ++

        // Numero di cifre giuste al posto sbagliato
        for (i in 0 until secret.value.length) {
            if (secret.value[i] != guess[i]) {
                newSecret += secret.value[i]
                newGuess += guess[i]
            }
        }

        if (!newSecret.isEmpty()) {
            for (letter in guess) {
                if (!evaluatedChars.contains(letter)) {
                    val howManyInSecret = countHowMany(newSecret, letter)
                    val howManyInGuess = countHowMany(newGuess, letter)

                    ngs += if (howManyInSecret == howManyInGuess || howManyInSecret > howManyInGuess) howManyInGuess
                    else howManyInSecret

                    evaluatedChars.add(letter)
                }
            }
        }

        attempts.add("${guess}/${ngg}/${ngs}")
        life.value -= 1

        if (ngg == secret.value.length)
            status.value = "win"
        else if (life.value < 1)
            status.value = "lose"
    }

    private fun countHowMany(letters: String, letter: Char): Int {
        var howMany = 0
        for (i in 0 until letters.length) {
            if (letters[i] == letter) {
                howMany++
            }
        }
        return howMany
    }




    fun loadMatch() {
        //TODO se ci va
    }
}
