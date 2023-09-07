package com.simoni.name.mastermind.model


import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.simoni.name.mastermind.db.Repository
import com.simoni.name.mastermind.model.utils.Attempt
import com.simoni.name.mastermind.model.utils.Difficulty
import com.simoni.name.mastermind.model.utils.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class InstantGame(private val repository: Repository) {
    var secret = mutableStateOf("XXXXX")
    var attempts = mutableStateListOf<Attempt>()
    var startTime = System.currentTimeMillis()
    var date = mutableStateOf(formatDate(System.currentTimeMillis()))
    var isGameFinished = mutableStateOf(false)
    var status = mutableStateOf(GameState.Load)
    var life = mutableIntStateOf(10)
    val colorOptions = listOf("B","R","O","Y","G","C","P","W")
    var duration= mutableLongStateOf(0L)
    var difficulty = mutableStateOf(Difficulty.Normal)
    var currentId: Long = -1L
    var isGameModified = mutableStateOf(false)


    init {
        CoroutineScope(Dispatchers.IO).launch {
            currentId = repository.getNextId() ?: 1L
        }
    }


    fun newMatch() {
        secret.value = generateRandomSecret()
        attempts.clear() // Resetta la lista di tentativi
        duration.longValue = 0L
        date.value = formatDate(System.currentTimeMillis())
        isGameFinished.value = false
        startTime = System.currentTimeMillis()
        status.value = GameState.Ongoing
        life.intValue = 10
    }

    private fun generateRandomSecret(): String {
        return if (difficulty.value == Difficulty.Normal) {
            buildString {
                repeat(5) {
                    append(colorOptions.random())
                }
            }
        } else {
            // Modalità Facile: Genera un codice segreto senza colori ripetuti
            val shuffledColors = colorOptions.shuffled().distinct()
            if (shuffledColors.size >= 5) {
                return shuffledColors.take(5).joinToString(separator = "")
            } else {
                return ""
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return dateFormat.format(calendar.time)
    }


    fun attempt(guess: String) {
        var nrr = 0
        var nrw = 0
        var newSecret = ""
        var newGuess = ""
        val evaluatedChars = mutableListOf<Char>()

        // Numero di cifre giuste al posto giusto
        for (i in 0 until secret.value.length) {
            if (secret.value[i] == guess[i]) {
                nrr++
            }
        }

        // Numero di cifre giuste al posto sbagliato
        for (i in 0 until secret.value.length) {
            if (secret.value[i] != guess[i]) {
                newSecret += secret.value[i]
                newGuess += guess[i]
            }
        }

        if (newSecret.isNotEmpty()) {
            for (letter in guess) {
                if (!evaluatedChars.contains(letter)) {
                    val howManyInSecret = countHowMany(newSecret, letter)
                    val howManyInGuess = countHowMany(newGuess, letter)

                    nrw += if (howManyInSecret == howManyInGuess || howManyInSecret > howManyInGuess) howManyInGuess
                    else howManyInSecret

                    evaluatedChars.add(letter)
                }
            }
        }

        attempts.add(Attempt(guess, nrr, nrw))
        life.intValue -= 1
        isGameModified.value = true

        if (nrr == secret.value.length) {
            status.value = GameState.Win
            isGameFinished.value = true
        } else if (life.intValue < 1) {
            status.value = GameState.Lose
            isGameFinished.value = true
        }
    }

    private fun countHowMany(letters: String, letter: Char): Int {
        var howMany = 0
        for (element in letters) {
            if (element == letter) {
                howMany++
            }
        }
        return howMany
    }
}