package com.simoni.name.mastermind

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.simoni.name.mastermind.db.DBMastermind
import com.simoni.name.mastermind.db.Repository
import com.simoni.name.mastermind.model.InstantGame
import com.simoni.name.mastermind.model.utils.MyViewState
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
                val db =  DBMastermind.getInstance(context)
                val repository = Repository(db.daoGameHistory())
                val instantGame = InstantGame(repository)
                //val vm : MyViewModel = ViewModelProvider(this)[MyViewModel(instantGame,repository,Application())::class.java]
                val vm = MyViewModel(instantGame,repository)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    when (vm.state.value) {
                        MyViewState.Init -> Home(vm)
                        MyViewState.Match -> GameView(vm)
                        MyViewState.History -> History(vm)
                    }
                }
            }
        }
    }
}