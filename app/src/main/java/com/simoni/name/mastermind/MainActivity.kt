package com.simoni.name.mastermind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
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
import com.simoni.name.mastermind.db.Repository
import com.simoni.name.mastermind.model.InstantGame
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.screen.*
import com.simoni.name.mastermind.ui.theme.Background
import com.simoni.name.mastermind.ui.theme.MasterMindTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.simoni.name.mastermind.db.Game


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
                val showDialog = remember { mutableStateOf(false) }
                val gameHistoryList = rememberSaveable { mutableStateOf<List<Game>>(emptyList()) }
                val dispatcher = LocalOnBackPressedDispatcherOwner.current
                val callback = object : OnBackPressedCallback(true){
                    override fun handleOnBackPressed() {
                        showDialog.value = true
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    // NavHost for the navigation trough the window
                    NavHost(navController = navController, startDestination = "Home") {
                        composable("Home") { Home(vm, navController, context, callback) }
                        composable("History") { History(vm, navController, context, gameHistoryList, callback) }
                        composable("GameView") { GameView(vm, navController, dispatcher, callback, showDialog, context) }
                    }
                }
            }
        }
    }
}
