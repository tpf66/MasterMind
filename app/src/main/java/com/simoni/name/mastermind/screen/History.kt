package com.simoni.name.mastermind.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.simoni.name.mastermind.R
import com.simoni.name.mastermind.db.Game
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.model.OrientationUtils
import com.simoni.name.mastermind.model.utils.GameState
import com.simoni.name.mastermind.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun History(
    vm: MyViewModel,
    navController: NavHostController,
    context: Context,
    gameHistoryList: MutableState<List<Game>>
) {
    val configuration = LocalConfiguration.current
    OrientationUtils.unlockOrientation(context as Activity)

    val stateLazy = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        gameHistoryList.value = withContext(Dispatchers.IO) {
            vm.getAllGameHistory()
        }
    }

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .bounceClickEffect(),
                        onClick = { navController.navigate("Home") },
                        colors = ButtonDefaults.buttonColors(Blue3),
                        shape = RoundedCornerShape(15.dp),
                        border = BorderStroke(3.dp, Green),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 10.dp,
                            pressedElevation = 15.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = W
                        )
                    }
                }

                if (gameHistoryList.value.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.history_empty),
                            color = W,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    coroutineScope.launch { stateLazy.animateScrollToItem(gameHistoryList.value.size) }

                    LazyColumn(
                        reverseLayout = true,
                        userScrollEnabled = true,
                        state = stateLazy
                        ) {
                        itemsIndexed(gameHistoryList.value) { _, it ->

                            GameHistoryItemRow(it, vm, navController)
                            { gameToDelete ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    vm.deleteSelectedGames(gameToDelete)
                                    gameHistoryList.value = vm.getAllGameHistory()
                                }
                                Toast.makeText(context, R.string.toast_delete , Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .bounceClickEffect(),
                        onClick = { navController.navigate("Home") },
                        colors = ButtonDefaults.buttonColors(Blue3),
                        shape = RoundedCornerShape(15.dp),
                        border = BorderStroke(3.dp, Green),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 10.dp,
                            pressedElevation = 15.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = W
                        )
                    }
                }

                if (gameHistoryList.value.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.history_empty),
                            color = W,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    coroutineScope.launch { stateLazy.animateScrollToItem(gameHistoryList.value.size) }

                    LazyColumn(
                        reverseLayout = true,
                        userScrollEnabled = true,
                        state = stateLazy
                    ) {
                        itemsIndexed(gameHistoryList.value) { _, it ->

                            GameHistoryItemRow(it, vm, navController)
                            { gameToDelete ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    vm.deleteSelectedGames(gameToDelete)
                                    gameHistoryList.value = vm.getAllGameHistory()
                                }
                                Toast.makeText(context, R.string.toast_delete , Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameHistoryItemRow(
    gameHistory: Game,
    vm: MyViewModel,
    navController: NavHostController,
    onClick: (Game) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Background3),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(5.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Row{
                    Text(
                        text = stringResource(id = R.string.secret_code),
                        modifier = Modifier.padding(2.dp),
                        fontSize = 12.sp,
                        color = W
                    )

                    for (color in gameHistory.secretCode) {
                        Canvas(
                            modifier = Modifier
                                .size(20.dp)
                                .padding(2.dp),
                            onDraw = {
                                drawCircle(
                                    color = if (gameHistory.result == GameState.Ongoing.toString()) Background2
                                    else colorForCode(color.toString()), // Funzione per ottenere il colore corrispondente
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

                Text(
                    text = stringResource(id = R.string.game_result) + gameHistory.result,
                    modifier = Modifier.padding(2.dp),
                    fontSize = 12.sp,
                    color = W
                )

                Text(
                    text = stringResource(id = R.string.difficulty) + gameHistory.difficulty,
                    modifier = Modifier.padding(2.dp),
                    fontSize = 12.sp,
                    color = W
                )

                Text(
                    text = stringResource(id = R.string.attempts) + gameHistory.numatt,
                    modifier = Modifier.padding(2.dp),
                    fontSize = 12.sp,
                    color = W
                )

                Text(
                    text = stringResource(id = R.string.game_duration) + formatHour(millis = gameHistory.duration),
                    modifier = Modifier.padding(2.dp),
                    fontSize = 12.sp,
                    color = W
                )

                Text(
                    text = stringResource(id = R.string.game_date) + formatDate(gameHistory.date),
                    modifier = Modifier.padding(2.dp),
                    fontSize = 12.sp,
                    color = W
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    modifier = Modifier
                        .padding(5.dp)
                        .bounceClickEffect(),
                    onClick = { onClick(gameHistory) },
                    colors = ButtonDefaults.buttonColors(Blue3),
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(3.dp, Green),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 15.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = W
                    )
                }
                if (gameHistory.result == GameState.Ongoing.toString()) {
                    Button(
                        modifier = Modifier
                            .padding(5.dp)
                            .bounceClickEffect(),
                        onClick = {
                            vm.loadGame(gameHistory)

                            onClick(gameHistory)
                            navController.navigate("GameView")
                        },
                        colors = ButtonDefaults.buttonColors(Blue3),
                        shape = RoundedCornerShape(15.dp),
                        border = BorderStroke(3.dp, Green),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 10.dp,
                            pressedElevation = 15.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = W
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return dateFormat.format(calendar.time)
}


@Composable
private fun formatHour(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%d:%d:%d", hours, minutes, seconds)
}

fun colorForCode(code: String): Color {
    return when (code) {
        "W" -> W
        "R" -> Re
        "C" -> C
        "G" -> G
        "Y" -> Y
        "P" -> P
        "O" -> O
        "B" -> B
        else -> Background2// Colore di default o gestire altri casi
    }
}