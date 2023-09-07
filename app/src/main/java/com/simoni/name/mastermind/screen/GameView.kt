package com.simoni.name.mastermind.screen

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.simoni.name.mastermind.R
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.model.utils.Attempt
import com.simoni.name.mastermind.model.utils.Difficulty
import com.simoni.name.mastermind.model.utils.GameState
import com.simoni.name.mastermind.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun GameView(
    vm: MyViewModel,
    navController: NavHostController,
    dispatcher: OnBackPressedDispatcherOwner?,
    callback: OnBackPressedCallback,
    showDialog: MutableState<Boolean>
) {
    dispatcher?.onBackPressedDispatcher?.addCallback(callback)
    val selectedColors = remember { mutableStateListOf("X", "X", "X", "X", "X") }
    val clickable = remember { mutableStateOf(true) }

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
        ColorSelection(vm)
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
        ButtonGuess(selectedColors)
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
                    text = if (vm.instantGame.status.value == GameState.Win) stringResource(id = R.string.game_result_win) else stringResource(id = R.string.game_result_lose) ,
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
                        text = stringResource(id = R.string.home),
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
            title = { Text(stringResource(id = R.string.dialog_save_game_title))},
            text = { Text(stringResource(id = R.string.dialog_save_game_message)) },
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
                    Text(stringResource(id = R.string.dialog_save))
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
                    Text(stringResource(id = R.string.dialog_no))
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
                            EmptyCircle(attempts[rowIndex].guess[i].toString()) {}
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
        val circle = mutableStateListOf(W, W, W, W, W)

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
        "R" -> Re
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
        "R" -> Re
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
        "R" -> Re
        "C" -> C
        "G" -> G
        "Y" -> Y
        "P" -> P
        "O" -> O
        "B" -> B
        else -> Color.Black// Colore di default o gestire altri casi
    }
}

