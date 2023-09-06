package com.simoni.name.mastermind.screen

import android.content.res.Configuration
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.simoni.name.mastermind.model.utils.Difficulty
import com.simoni.name.mastermind.model.utils.GameState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun GameView(
    vm: MyViewModel,
    navController: NavHostController,
    dispatcher: OnBackPressedDispatcherOwner?,
    callback: OnBackPressedCallback,
    showDialog: MutableState<Boolean>
) {
    dispatcher?.onBackPressedDispatcher?.addCallback(callback)
    val selectedColors = remember { mutableStateListOf<String>("X", "X", "X", "X", "X") }
    val clickable = remember { mutableStateOf<Boolean>(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = {
                    if (clickable.value) {
                        showDialog.value = true
                    }
                },
                colors = ButtonDefaults.buttonColors(Blue3),
                shape = RoundedCornerShape(15.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = W
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = formatHour(vm.instantGame.duration.value),
                color = W,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.weight(0.5f))

        // Area di gioco con tentativi
        GameArea(vm.instantGame.attempts, selectedColors)
        { i ->
            if (clickable.value) {
                selectedColors[i] = "X"
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Sezione di selezione dei colori
        ColorSelection(vm, selectedColors)
        { color ->
            if (selectedColors.contains("X") && clickable.value) {
                for (i in 0 until selectedColors.size) {
                    if (selectedColors[i] == "X") {
                        if (vm.instantGame.difficulty.value == Difficulty.Easy && !selectedColors.contains(
                                color
                            )
                        )
                            selectedColors[i] = color
                        else if (vm.instantGame.difficulty.value == Difficulty.Normal)
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
            if (!selectedColors.contains("X") && clickable.value) {
                val reset = listOf("X", "X", "X", "X", "X")
                vm.instantGame.attempt(selectedColor.joinToString(separator = ""))
                selectedColors.clear()
                selectedColors.addAll(reset)
            }
        }
    }
    if (vm.instantGame.isGameFinished.value) {
        clickable.value = false

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)) // Sfondo semitrasparente
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(W)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (vm.instantGame.status.value == GameState.Win) "You Win!" else "You Lose!",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (color in vm.instantGame.secret.value) {
                        Canvas(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(2.dp),
                            onDraw = {
                                drawCircle(
                                    color = getColorForCode(color.toString()), // Funzione per ottenere il colore corrispondente
                                    radius = size.minDimension / 2
                                )
                                drawCircle(
                                    color = Color.Black,
                                    radius = size.minDimension / 2,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        )
                    }
                }

                // Aggiungi il pulsante "Home"
                Button(
                    onClick = {
                        navController.navigate("Home")
                        vm.saveOnDb()
                    },
                    colors = ButtonDefaults.buttonColors(Blue2),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = W
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Home",
                        color = W
                    )
                }
            }
        }
    } else {
        clickable.value = true
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Chiudi il dialog senza salvare
                showDialog.value = false
            },
            title = { Text("Salvare la partita?") },
            text = { Text("La partita non è stata completata. Vuoi salvarla prima di uscire?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Chiama la funzione per salvare la partita sul database
                        vm.saveOnDb()
                        vm.instantGame.status.value = GameState.Load

                        // Esegui le azioni di navigazione desiderate
                        navController.navigate("Home")

                        // Chiudi il dialog
                        showDialog.value = false
                    }
                ) {
                    Text("Salva")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // Esegui le azioni di navigazione desiderate
                        navController.navigate("Home")

                        // non salvare la partita
                        vm.instantGame.status.value = GameState.Load

                        // Chiudi il dialog senza salvare
                        showDialog.value = false
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
private fun formatHour(timestamp: Long): String {
    val hourFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return hourFormat.format(calendar.time)
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
            val myModifier: Modifier = if (rowIndex == attempts.size) {
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(2.dp)
                    .border(BorderStroke(2.dp, Blue4), shape = RoundedCornerShape(15.dp))
            } else
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(2.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = myModifier,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (rowIndex < attempts.size) {
                        for (i in 0 until 5) {
                            EmptyCircle(attempts[rowIndex].guess.get(i).toString()) {}
                        }
                    } else if (rowIndex == attempts.size) {
                        for (i in 0 until 5)
                            EmptyCircle(selectedColors[i], onClick = { onClick(i) })
                    } else {
                        for (i in 0 until 5)
                            EmptyCircle(selectedColor = "X") {}
                    }
                }


                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (rowIndex < attempts.size)
                        FeedBack(
                            attempts[rowIndex].rightNumRightPos,
                            attempts[rowIndex].rightNumWrongPos
                        )
                    else
                        FeedBack(0, 0)
                }
            }
        }
    }
}

@Composable
fun FeedBack(nrr: Int, nrw: Int) {
    Canvas(
        modifier = Modifier
            .size(50.dp)
    ) {
        val radius = 5.dp.toPx()
        val border = 1.dp.toPx()
        val circle = mutableStateListOf<Color>(W, W, W, W, W)

        for (i in 0 until nrr)
            circle[i] = Blue5
        for (i in nrr until nrr + nrw)
            circle[i] = Blue3
        for (i in nrr + nrw until 5)
            circle[i] = Color.Transparent


        translate(left = -35f, top = -20f) {

            drawCircle(
                color = Background2,
                radius = radius,
                //style = Stroke(width = border)
            )
            drawCircle(
                color = circle[0],
                radius = radius
            )
        }
        translate(left = 0f, top = -20f) {

            drawCircle(
                color = Background2,
                radius = radius,
                //style = Stroke(width = border)
            )
            drawCircle(
                color = circle[1],
                radius = radius,
            )
        }
        translate(left = 35f, top = -20f) {

            drawCircle(
                color = Background2,
                radius = radius,
                //style = Stroke(width = border)
            )
            drawCircle(
                color = circle[2],
                radius = radius,
            )
        }
        translate(left = -20f, top = 20f) {

            drawCircle(
                color = Background2,
                radius = radius,
                //style = Stroke(width = border)
            )
            drawCircle(
                color = circle[3],
                radius = radius
            )
        }
        translate(left = 20f, top = 20f) {

            drawCircle(
                color = Background2,
                radius = radius,
                //style = Stroke(width = border)
            )
            drawCircle(
                color = circle[4],
                radius = radius
            )
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
        else -> Background2
    }

    Canvas(
        modifier = Modifier
            .size(40.dp)
            .padding(5.dp)
            .clickable { onClick() }
            .border(3.dp, Color.Black, shape = CircleShape)
    ) {
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
            .padding(bottom = 15.dp),
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
                    .border(1.dp, shape = CircleShape, color = Color.Black)
                    .background(
                        color = colorValue,
                        shape = CircleShape
                    )
            )
        }
    )
}

fun getColorForCode(code: String): Color {
    return when (code) {
        "W" -> W
        "R" -> R
        "C" -> C
        "G" -> G
        "Y" -> Y
        "P" -> P
        "O" -> O
        "B" -> B
        else -> Color.Black// Colore di default o gestire altri casi
    }
}


/*@Preview(showBackground = true)
@Composable
fun GreetingPreviewGame() {
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
            //GameView(vm = vm, navController = navController, callback = callback)
        }
    }
}*/
