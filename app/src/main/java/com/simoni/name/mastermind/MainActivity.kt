package com.simoni.name.mastermind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.simoni.name.mastermind.db.*
import com.simoni.name.mastermind.model.InstantGame
import com.simoni.name.mastermind.model.MyState
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.screen.*
import com.simoni.name.mastermind.ui.theme.Background
import com.simoni.name.mastermind.ui.theme.MasterMindTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MasterMindTheme {
                val context = LocalContext.current
                val db =  DbGame.getInstance(context)
                val repository = Repository(db.gameDao())
                var instantGame = InstantGame()
                val vm : MyViewModel = MyViewModel(instantGame, repository)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    when (vm.state.value) {
                        MyState.Init -> Home(vm)
                        MyState.Match -> GameView(vm)
                        MyState.History -> History(vm)
                    }
                }
            }
        }
    }
}