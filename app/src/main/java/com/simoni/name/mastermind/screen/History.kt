package com.simoni.name.mastermind.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
    gameHistoryList: MutableState<List<Game>>,
    callback: OnBackPressedCallback
) {
    val configuration = LocalConfiguration.current
    val stateLazy = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    OrientationUtils.unlockOrientation(context as Activity)

    // diable the backpres function
    callback.isEnabled = false

    // Load the history from the db
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
                    // Button home
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
                    // empty history
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

                            // lazy column with each game
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

        // same thing with landscape mode
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

// Item
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
                // Secret code
                Row{
                    Text(
                        text = stringResource(id = R.string.secret_code),
                        modifier = Modifier.padding(2.dp),
                        fontSize = 12.sp,
                        color = W
                    )

                    // i draw the sequence if the game is finished
                    for (color in gameHistory.secretCode) {
                        Canvas(
                            modifier = Modifier
                                .size(20.dp)
                                .padding(2.dp),
                            onDraw = {
                                drawCircle(
                                    color = if (gameHistory.result == GameState.Ongoing.toString()) Background2
                                    else colorForCode(color.toString()), // function to obtain the corresponding color
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

                // Result game
                Text(
                    text = stringResource(id = R.string.game_result) + gameHistory.result,
                    modifier = Modifier.padding(2.dp),
                    fontSize = 12.sp,
                    color = W
                )

                // Difficulty
                Text(
                    text = stringResource(id = R.string.difficulty) + gameHistory.difficulty,
                    modifier = Modifier.padding(2.dp),
                    fontSize = 12.sp,
                    color = W
                )

                // Attempts
                Text(
                    text = stringResource(id = R.string.attempts) + gameHistory.numatt,
                    modifier = Modifier.padding(2.dp),
                    fontSize = 12.sp,
                    color = W
                )

                // Duration
                Text(
                    text = stringResource(id = R.string.game_duration) + formatHour(millis = gameHistory.duration),
                    modifier = Modifier.padding(2.dp),
                    fontSize = 12.sp,
                    color = W
                )

                // Date
                Text(
                    text = stringResource(id = R.string.game_date) + formatDate(gameHistory.date),
                    modifier = Modifier.padding(2.dp),
                    fontSize = 12.sp,
                    color = W
                )
            }

            // draw the button of the action of the game
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                // delete button
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
                    // play button only if isn't finished
                    Button(
                        modifier = Modifier
                            .padding(5.dp)
                            .bounceClickEffect(),
                        onClick = {
                            vm.loadGame(gameHistory)
                            navController.navigate("GameView")
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.deleteSelectedGames(gameHistory)
                            }
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

// function for visualization
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