package com.simoni.name.mastermind.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.simoni.name.mastermind.db.Game
import com.simoni.name.mastermind.db.Repository
import com.simoni.name.mastermind.model.utils.Attempt
import com.simoni.name.mastermind.model.utils.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class MyViewModel(inGame: InstantGame, repo: Repository) {
    var instantGame : InstantGame
    private var repository : Repository

    init {
        instantGame = inGame
        repository = repo
    }

    // create new game
    fun newGame() {
        if (instantGame.status.value != GameState.Ongoing) {
            instantGame.newMatch()

            CoroutineScope(Dispatchers.Default).launch {
                while (instantGame.status.value == GameState.Ongoing) {
                    instantGame.duration.longValue =
                        System.currentTimeMillis() - instantGame.startTime.absoluteValue
                    withContext(Dispatchers.IO) {
                        Thread.sleep(500)
                    }
                }
            }
        }
    }

    // save game on db
    fun saveOnDb (){
        val game = Game(
            id = instantGame.currentId++,
            version = "1.0",
            secretCode = instantGame.secret.value,
            result = instantGame.status.value.toString(),
            stratt = instantGame.attempts.toList().toString(),
            numatt = instantGame.attempts.size,
            duration = instantGame.duration.longValue,
            date = System.currentTimeMillis(),
            difficulty = instantGame.difficulty.value
        )

        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(game)
        }
    }

    // load game from db
    fun loadGame(game: Game) {
        newGame()

        instantGame.secret.value = game.secretCode
        instantGame.attempts = toAttempt(game.stratt, game.numatt) // Resetta la lista di tentativi
        instantGame.duration.longValue = 0L
        instantGame.date.value = formatDate(System.currentTimeMillis())
        instantGame.isGameFinished.value = false
        instantGame.startTime = System.currentTimeMillis() - game.duration
        instantGame.status.value = GameState.Ongoing
        instantGame.life.intValue = 10-game.numatt
        instantGame.difficulty.value = game.difficulty
        instantGame.currentId = game.id
        instantGame.isGameModified.value = false
        instantGame.loaded.value = true
    }

    // get the history value
    suspend fun getAllGameHistory(): List<Game> {
        return withContext(Dispatchers.IO) {
            repository.readAll()
        }
    }

    // delete a game from db
    suspend fun deleteSelectedGames(game: Game) {
        repository.deleteGameHistory(game)
    }
}


// function to convert the attempt saved on db in Attempt
fun toAttempt (stratt : String, numatt : Int) : SnapshotStateList<Attempt> {
    val attempts = mutableStateListOf<Attempt>()
    var guess: String
    var nrr: Int
    var nrw: Int
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


// format date
private fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return dateFormat.format(calendar.time)
}