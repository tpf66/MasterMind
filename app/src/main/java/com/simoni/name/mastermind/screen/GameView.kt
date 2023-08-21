package com.simoni.name.mastermind.screen

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.simoni.name.mastermind.db.DBMastermind
import com.simoni.name.mastermind.db.Repository
import com.simoni.name.mastermind.model.InstantGame
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.model.utils.Attempt
import com.simoni.name.mastermind.ui.theme.*
import androidx.compose.foundation.Canvas

@Composable
fun GameView(vm: MyViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            val selectedColors = remember { mutableStateListOf<String>("X","X","X","X","X") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.weight(0.5f))

                // Area di gioco con tentativi
                GameArea(vm.instantGame.attempts, selectedColors)
                { i ->
                    selectedColors[i] = "X"
                }

                Spacer(modifier = Modifier.weight(0.5f))

                // Sezione di selezione dei colori
                ColorSelection(vm, selectedColors)
                { color ->
                    if (selectedColors.size < 8) {
                        for (i in 0 until selectedColors.size){
                            if (selectedColors[i] == "X"){
                                selectedColors[i] = color
                                break
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(0.2f))

                // Pulsante Submit
                ButtonGuess(vm, selectedColors)
                { selectedColor ->
                    val reset = listOf("X","X","X","X","X")
                    vm.instantGame.attempt(selectedColor.joinToString(separator = ""))
                    selectedColors.clear()
                    selectedColors.addAll(reset)
                }
            }
        }

        else -> {
        }
    }
}



@Composable
fun ButtonGuess(
    vm: MyViewModel,
    selectedColors: SnapshotStateList<String>,
    onClick: (SnapshotStateList<String>) -> Unit
) {
    Button(
        onClick = { onClick(selectedColors) },
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Blue3),
    ) {
        Text(
            text = "Submit",
            color = W
        )
    }
}


@Composable
fun GameArea(
    attempts: List<Attempt>,
    selectedColors: List<String>,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        for (rowIndex in 0 until 10) {
            if (rowIndex == attempts.size){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(BorderStroke(3.dp, Color.Black), shape = RoundedCornerShape(15.dp)) ,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (rowIndex < attempts.size) {
                    for (i in 0 until 5) {
                        EmptyCircle(attempts[rowIndex].guess.get(i).toString()){}
                    }
                }else if (rowIndex == attempts.size){
                    for (i in 0 until 5)
                        EmptyCircle(selectedColors[i], onClick = { onClick(i) })
                }else{
                    for (i in 0 until 5)
                        EmptyCircle(selectedColor = "X"){}
                }
            }
            }else{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (rowIndex < attempts.size) {
                        for (i in 0 until 5) {
                            EmptyCircle(attempts[rowIndex].guess.get(i).toString()){}
                        }
                    }else if (rowIndex == attempts.size){
                        for (i in 0 until 5)
                            EmptyCircle(selectedColors[i], onClick = { onClick(i) })
                    }else{
                        for (i in 0 until 5)
                            EmptyCircle(selectedColor = "X"){}
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun EmptyCircle(
    selectedColor: String?,
    onClick: () -> Unit
) {
    val colorValue = when (selectedColor) {
        "W" -> W
        "R" -> R
        "C" -> C
        "G" -> G
        "Y" -> Y
        "P" -> P
        "O" -> O
        "B" -> B
        else -> Color.Transparent
    }

    Canvas(
        modifier = Modifier
            .size(30.dp)
            .clickable { onClick() }
            .border(3.dp, Color.Black, shape = CircleShape)
    ){
        drawCircle(
            color = colorValue
        )
    }
}


@Composable
fun ColorSelection(
    vm: MyViewModel,
    selectedColors: MutableList<String>,
    onClick: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (color in vm.instantGame.colorOptions) {
            ColorButton(
                color = color,
                onClick = { onClick(color) }
            )
        }
    }
}


@Composable
fun ColorButton(
    color: String,
    onClick: () -> Unit
) {
    val colorValue = when (color) {
        "W" -> W
        "R" -> R
        "C" -> C
        "G" -> G
        "Y" -> Y
        "P" -> P
        "O" -> O
        "B" -> B
        else -> Background
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center,
        content = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = colorValue,
                        shape = CircleShape
                    )
            )
        }
    )
}














@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val context = LocalContext.current
    val db = DBMastermind.getInstance(context)
    val repository = Repository(db.daoGameHistory())
    val instantGame = InstantGame(repository)
    val vm = MyViewModel(instantGame, repository)
    val navController = rememberNavController()

    MasterMindTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {
            GameView(vm = vm, navController = navController)
        }
    }
}
