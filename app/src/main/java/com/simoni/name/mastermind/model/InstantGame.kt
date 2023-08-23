package com.simoni.name.mastermind.model


import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.simoni.name.mastermind.db.Game
import com.simoni.name.mastermind.db.Repository
import com.simoni.name.mastermind.model.utils.Attempt
import com.simoni.name.mastermind.model.utils.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue


class InstantGame(private val repository: Repository) {
    var secret = mutableStateOf("XXXXX")
    var attempts = mutableStateListOf<Attempt>()
    var startTime = System.currentTimeMillis()
    var date = mutableStateOf(formatDate(System.currentTimeMillis()))
    var isGameFinished = mutableStateOf(false)
    var status = mutableStateOf(GameState.Load)
    var life = mutableIntStateOf(10)
    val colorOptions = listOf("B","R","O","Y","G","C","P","W")
    var duration= mutableStateOf(0L)
    var currentId: Long = -1L


    init {
        CoroutineScope(Dispatchers.IO).launch {
            currentId = repository.getNextId() ?: 1L
        }
    }


    fun newMatch() {
        secret.value = generateRandomSecret()
        attempts.clear() // Resetta la lista di tentativi
        duration.value = 0L
        date.value = formatDate(System.currentTimeMillis())
        isGameFinished.value = false
        startTime = System.currentTimeMillis()
        status.value = GameState.Ongoing
    }

    private fun generateRandomSecret(): String {
        return buildString {
            repeat(5) {
                append(colorOptions.random())
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
        var nrr: Int = 0
        var nrw: Int = 0
        var newSecret = ""
        var newGuess = ""
        var attempt: Attempt
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

        if (!newSecret.isEmpty()) {
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
        life.value -= 1

        if (nrr == secret.value.length) {
            status.value = GameState.Win
            isGameFinished.value = true
        } else if (life.value < 1) {
            status.value = GameState.Lose
            isGameFinished.value = true
        }
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


    fun saveOnDb() {
        duration.value = System.currentTimeMillis() - startTime
        val game = Game(
            id = currentId++,
            version = "1.0",
            secretCode = secret.value,
            result = if (isGameFinished.value) "Game Over" else "Correct Combination!",
            attempts = attempts.size,
            duration = duration.value,
            date = System.currentTimeMillis()
        )

        runBlocking {
            withContext(Dispatchers.IO) {
                repository.insert(game)
            }
        }
    }


    fun loadMatch() {
        //TODO se ci va
    }
}