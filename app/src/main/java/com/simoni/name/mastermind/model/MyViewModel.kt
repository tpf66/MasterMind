package com.simoni.name.mastermind.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.simoni.name.mastermind.db.Game
import com.simoni.name.mastermind.db.Repository
import com.simoni.name.mastermind.model.utils.Attempt
import com.simoni.name.mastermind.model.utils.Difficulty
import com.simoni.name.mastermind.model.utils.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue

class MyViewModel(inGame: InstantGame, repo: Repository) {
    var instantGame : InstantGame
    var repository : Repository

    init {
        instantGame = inGame
        repository = repo
    }

    fun newGame(){
        if (instantGame.status.value != GameState.Ongoing) {
            instantGame.newMatch()

            CoroutineScope(Dispatchers.Default).launch {
                while (instantGame.status.value == GameState.Ongoing) {
                    instantGame.duration.value =
                        System.currentTimeMillis() - instantGame.startTime.absoluteValue
                    Thread.sleep(500)
                }
            }
        }
    }

    fun saveOnDb (){
        val game = Game(
            id = instantGame.currentId++,
            version = "1.0",
            secretCode = instantGame.secret.value,
            result = instantGame.status.value.toString(),
            stratt = instantGame.attempts.toList().toString(),
            numatt = instantGame.attempts.size,
            duration = instantGame.duration.value,
            date = System.currentTimeMillis(),
            difficulty = instantGame.difficulty.value
        )

        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(game)
        }
    }

    fun loadGame(game: Game) {
        newGame()

        instantGame.secret.value = game.secretCode
        instantGame.attempts = toAttempt(game.stratt, game.numatt) // Resetta la lista di tentativi
        instantGame.duration.value = game.duration
        instantGame.date.value = formatDate(System.currentTimeMillis())
        instantGame.isGameFinished.value = false
        instantGame.startTime = System.currentTimeMillis()
        instantGame.status.value = GameState.Ongoing
        instantGame.life.value = 10-game.numatt
        instantGame.difficulty.value = game.difficulty
        instantGame.currentId = game.id
        instantGame.isGameModified.value = false
    }

    suspend fun getAllGameHistory(): List<Game> {
        return withContext(Dispatchers.IO) {
            repository.readAll()
        }
    }

    suspend fun deleteSelectedGames(game: Game) {
        repository.deleteGameHistory(game)
    }
}


fun toAttempt (stratt : String, numatt : Int) : SnapshotStateList<Attempt> {
    val attempts = mutableStateListOf<Attempt>()
    var guess : String = ""
    var nrr : Int = 0
    var nrw : Int = 0
    var string : String

    for (i in 0 until numatt){
        string = stratt

        repeat(i) {
            string = string.substringAfter(")")
        }

        string = string.substringAfter("(").substringBefore(')')

        guess = string.substringAfter("guess=").substringBefore(',')
        nrr = string.substringAfter("rightNumRightPos=").substringBefore(',').toInt()
        nrw = string.substringAfter("rightNumWrongPos=").toInt()

        attempts.add(Attempt(guess, nrr, nrw))
    }

    return attempts
}


private fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return dateFormat.format(calendar.time)
}