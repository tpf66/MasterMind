package com.simoni.name.mastermind.screen

import android.content.res.Configuration
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.simoni.name.mastermind.model.IstantGame
import com.simoni.name.mastermind.model.MyState
import com.simoni.name.mastermind.model.MyViewModel

@Composable
fun Home(vm: MyViewModel) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            TODO()
            Button(onClick = { vm.hi() }) {
                Text(text = "history")
            }
        }
        else -> {
        }
    }
}
