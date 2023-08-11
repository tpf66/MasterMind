package com.simoni.name.mastermind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.simoni.name.mastermind.model.IstantGame
import com.simoni.name.mastermind.screen.*
import com.simoni.name.mastermind.ui.theme.MasterMindTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.simoni.name.mastermind.model.MyState.*
import com.simoni.name.mastermind.model.MyViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MasterMindTheme {
                //val context = LocalContext.current
                //val db =  DbGame.getInstance(context)
                //val repository = Repository(db.gameDao())
                var instantGame by rememberSaveable { mutableStateOf(IstantGame(this)) }
                val vm : MyViewModel = MyViewModel(instantGame)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (vm.state.value) {
                        Init -> Home(vm)
                        NewGame -> GameView(vm)
                        History -> History(vm)
                    }
                }
            }
        }
    }
}