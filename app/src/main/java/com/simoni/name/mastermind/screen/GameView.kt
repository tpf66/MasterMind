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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun GameView(vm: MyViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            val selectedColors = remember { mutableStateListOf<String>("X", "X", "X", "X", "X") }

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
                            navController.navigate("Home") },
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
                    selectedColors[i] = "X"
                }

                Spacer(modifier = Modifier.weight(0.5f))

                // Sezione di selezione dei colori
                ColorSelection(vm, selectedColors)
                { color ->
                    if (selectedColors.size < 8) {
                        for (i in 0 until selectedColors.size) {
                            if (selectedColors[i] == "X") {
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
                    val reset = listOf("X", "X", "X", "X", "X")
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
                    modifier = Modifier.fillMaxWidth(0.5f),
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
        Canvas(modifier = Modifier
            .size(50.dp)
        ) {
            val radius = 5.dp.toPx()
            val circle = mutableStateListOf<Color>(W,W,W,W,W)

            for (i in 0 until nrr)
                circle[i] = Blue5
            for (i in nrr until nrr+nrw)
                circle[i] = Blue3
            for (i in nrr+nrw until 5)
                circle[i] = Color.Transparent


            translate(left = -30f, top = -15f) {
                drawCircle(
                    color = circle[0],
                    radius = radius,
                )
            }
            translate(left = 0f, top = -15f) {
                drawCircle(
                    color = circle[1],
                    radius = radius,
                )
            }
            translate(left = 30f, top = -15f) {
                drawCircle(
                    color = circle[2],
                    radius = radius,
                )
            }
            translate(left = -15f, top = 15f) {
                drawCircle(
                    color = circle[3],
                    radius = radius
                )
            }
            translate(left = 15f, top = 15f) {
                drawCircle(
                    color = circle[4],
                    radius = radius
                )
            }
        }

}

/*@Preview(showBackground = true)
@Composable
fun GreetingPreviewFeedback() {
    MasterMindTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {
            FeedBack(nrr = 1, nrw = 4)
        }
    }
}*/

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


@Preview(showBackground = true)
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
            GameView(vm = vm, navController = navController)
        }
    }
}
