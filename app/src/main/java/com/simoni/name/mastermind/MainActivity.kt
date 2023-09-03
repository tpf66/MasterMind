package com.simoni.name.mastermind

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.simoni.name.mastermind.db.DBMastermind
import com.simoni.name.mastermind.db.Game
import com.simoni.name.mastermind.db.Repository
import com.simoni.name.mastermind.model.InstantGame
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.screen.*
import com.simoni.name.mastermind.ui.theme.Background
import com.simoni.name.mastermind.ui.theme.MasterMindTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.simoni.name.mastermind.model.utils.Attempt
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MasterMindTheme {
                val context = LocalContext.current
                val db = DBMastermind.getInstance(context)
                val repository = Repository(db.daoGameHistory())
                val instantGame by remember { mutableStateOf(InstantGame(repository)) }
                val vm by remember { mutableStateOf(MyViewModel(instantGame, repository)) }
                val navController = rememberNavController()
                val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                var first  = true

                /*if(first) {
                    first = false
                    val game = Game(
                        id = 1,
                        version = "1.0",
                        secretCode = instantGame.secret.value,
                        stratt = "",
                        numatt = 0,
                        result = "win",
                        //attempts = 3,
                        duration = 3,
                        date = System.currentTimeMillis()
                    )

                    runBlocking {
                        withContext(Dispatchers.IO) {
                            repository.deleteAllGameHistory()
                            repository.insert(game)
                        }
                    }
                }*/

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    NavHost(navController = navController, startDestination = "Home") {
                        composable("Home") { Home(vm, navController) }
                        composable("History") { History(vm, navController) }
                        composable("Settings") { Settings(vm, navController) }
                        composable("GameView") {
                            if (dispatcher != null) {
                                GameView(vm, navController, dispatcher)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun fromAttempt(attempts: SnapshotStateList<Attempt>): String {
    return attempts.toList().toString()
}