package com.simoni.name.mastermind.model

import com.simoni.name.mastermind.db.Game
import com.simoni.name.mastermind.db.Repository
import com.simoni.name.mastermind.model.utils.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                while (true) {
                    if (instantGame.status.value == GameState.Ongoing)
                        instantGame.duration.value =
                            System.currentTimeMillis() - instantGame.startTime.absoluteValue
                    Thread.sleep(500)
                }
            }
        }
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
