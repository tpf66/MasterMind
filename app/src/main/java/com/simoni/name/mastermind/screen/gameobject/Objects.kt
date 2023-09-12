package com.simoni.name.mastermind.screen.gameobject

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
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
import com.simoni.name.mastermind.model.utils.GameState
import com.simoni.name.mastermind.screen.bounceClickEffect
import com.simoni.name.mastermind.ui.theme.B
import com.simoni.name.mastermind.ui.theme.Background
import com.simoni.name.mastermind.ui.theme.Background2
import com.simoni.name.mastermind.ui.theme.Black
import com.simoni.name.mastermind.ui.theme.Blue1
import com.simoni.name.mastermind.ui.theme.Blue2
import com.simoni.name.mastermind.ui.theme.Blue3
import com.simoni.name.mastermind.ui.theme.Blue5
import com.simoni.name.mastermind.ui.theme.C
import com.simoni.name.mastermind.ui.theme.G
import com.simoni.name.mastermind.ui.theme.O
import com.simoni.name.mastermind.ui.theme.P
import com.simoni.name.mastermind.ui.theme.Re
import com.simoni.name.mastermind.ui.theme.Transparent
import com.simoni.name.mastermind.ui.theme.W
import com.simoni.name.mastermind.ui.theme.Y
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// dialog on game finish
@Composable
fun OnFinish(
    vm: MyViewModel,
    navController: NavHostController,
    clickable: MutableState<Boolean>
) {
    clickable.value = false

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black.copy(alpha = 0.7f)) // Background transparent
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
                text = if (vm.instantGame.status.value == GameState.Win) stringResource(id = R.string.game_result_win) else stringResource(
                    id = R.string.game_result_lose
                ),
                color = Black,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // print the correct sequence
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
                                color = Black,
                                radius = size.minDimension / 2,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    )
                }
            }

            // Home button
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
}


@Composable
fun Dialog(
    vm: MyViewModel,
    navController: NavHostController,
    showDialog: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = {
            // close the dialog without saving
            showDialog.value = false
        },
        title = { Text(stringResource(id = R.string.dialog_save_game_title)) },
        text = {
            Text(
                if (vm.instantGame.loaded.value) stringResource(id = R.string.dialog_save_game_loaded) else stringResource(
                    id = R.string.dialog_save_game_message
                )
            )
        },
        containerColor = W,
        textContentColor = Black,
        titleContentColor = Black,
        confirmButton = {
            TextButton(
                onClick = {
                    // save the game on the db
                    vm.saveOnDb()
                    vm.instantGame.status.value = GameState.Load

                    // go to the home window
                    navController.navigate("Home")

                    // Close the dialog
                    showDialog.value = false
                }
            ) {
                Text(stringResource(id = R.string.dialog_save), color = Blue1)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    // go to the home
                    navController.navigate("Home")

                    // do not save the game
                    vm.instantGame.status.value = GameState.Load

                    // close the dialog
                    showDialog.value = false
                }
            ) {
                Text(stringResource(id = R.string.dialog_no), color = Blue1)
            }
        }
    )
}


@Composable
fun formatHour(timestamp: Long): String {
    val hourFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return hourFormat.format(calendar.time)
}

// Button for guess the color
@Composable
fun ButtonGuess(
    selectedColors: SnapshotStateList<String>,
    onClick: (SnapshotStateList<String>) -> Unit
) {
    Button(
        onClick = { onClick(selectedColors) },
        colors = ButtonDefaults.buttonColors(Blue2),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(3.dp, Blue1),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .bounceClickEffect()
    ) {
        Text(
            text = stringResource(id = R.string.button_submit),
            color = W
        )
    }
}

// draw the feedback hint with color ball
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
            circle[i] = Transparent


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

// empty attempt
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
            .border(3.dp, Black, shape = CircleShape)
    ) {
        drawCircle(
            color = colorValue
        )
    }
}

// Buttons for selecting the colors
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

// each ball with color
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
                    .border(1.dp, shape = CircleShape, color = Black)
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
        else -> Black// Colore di default o gestire altri casi
    }
}
