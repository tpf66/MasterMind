package com.simoni.name.mastermind.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.simoni.name.mastermind.db.DBMastermind
import com.simoni.name.mastermind.db.Game
import com.simoni.name.mastermind.db.Repository
import com.simoni.name.mastermind.model.InstantGame
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.ui.theme.Background
import com.simoni.name.mastermind.ui.theme.Blue3
import com.simoni.name.mastermind.ui.theme.MasterMindTheme
import com.simoni.name.mastermind.ui.theme.W
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.simoni.name.mastermind.model.utils.GameState
import com.simoni.name.mastermind.ui.theme.B
import com.simoni.name.mastermind.ui.theme.Background2
import com.simoni.name.mastermind.ui.theme.C
import com.simoni.name.mastermind.ui.theme.G
import com.simoni.name.mastermind.ui.theme.O
import com.simoni.name.mastermind.ui.theme.P
import com.simoni.name.mastermind.ui.theme.R
import com.simoni.name.mastermind.ui.theme.Y


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun History(vm: MyViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            var gameHistoryList by remember { mutableStateOf<List<Game>>(emptyList()) }
            val stateLazy = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                gameHistoryList = withContext(Dispatchers.IO) {
                    vm.getAllGameHistory()
                }
            }

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
                        modifier = Modifier.padding(16.dp),
                        onClick = { navController.navigate("Home") },
                        colors = ButtonDefaults.buttonColors(Blue3),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = W
                        )
                    }
                }

                if (gameHistoryList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Empty history",
                            color = W,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    coroutineScope.launch { stateLazy.animateScrollToItem(gameHistoryList.size) }

                    LazyColumn(
                        reverseLayout = true,
                        userScrollEnabled = true,
                        state = stateLazy,

                        ) {
                        itemsIndexed(gameHistoryList) { index, it ->

                            GameHistoryItemRow(
                                gameHistory = it,
                                vm,
                                navController
                            ) { gameToDelete ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    vm.deleteSelectedGames(gameToDelete)
                                    gameHistoryList = vm.getAllGameHistory()
                                }
                            }
                        }
                    }
                }
            }
        }

        else -> {
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
                        text = "Codice segreto: ",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(2.dp),
                        color = Color.Black
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
                                    color = Color.Black,
                                    radius = size.minDimension / 2,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        )
                    }
                }

                Text(
                    text = "Risultato: ${gameHistory.result}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(2.dp),
                    color = Color.Black
                )

                Text(
                    text = "DIfficoltà: ${gameHistory.difficulty}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(2.dp),
                    color = Color.Black
                )

                Text(
                    text = "Tentativi: ${gameHistory.numatt}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(2.dp),
                    color = Color.Black
                )

                Text(
                    text = "Durata Partita: ${formatHour(timestamp = gameHistory.duration)}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(2.dp),
                    color = Color.Black
                )

                Text(
                    text = "Data Partita: ${formatDate(gameHistory.date)}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(2.dp),
                    color = Color.Black
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    modifier = Modifier.padding(5.dp),
                    onClick = { onClick(gameHistory) },
                    colors = ButtonDefaults.buttonColors(Blue3),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = W
                    )
                }
                if (gameHistory.result == GameState.Ongoing.toString()) {
                    Button(
                        modifier = Modifier.padding(5.dp),
                        onClick = {
                            vm.loadGame(gameHistory)

                            onClick(gameHistory)
                            navController.navigate("GameView")
                        },
                        colors = ButtonDefaults.buttonColors(Blue3),
                        shape = RoundedCornerShape(15.dp)
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
private fun formatHour(timestamp: Long): String {
    val hourFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return hourFormat.format(calendar.time)
}

fun colorForCode(code: String): Color {
    return when (code) {
        "W" -> W
        "R" -> R
        "C" -> C
        "G" -> G
        "Y" -> Y
        "P" -> P
        "O" -> O
        "B" -> B
        else -> Background2// Colore di default o gestire altri casi
    }
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
    var gameHistoryList by remember { mutableStateOf<List<Game>>(emptyList()) }

    val stateLazy = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val game = Game(
        id = 1,
        version = "1.0",
        secretCode = instantGame.secret.value,
        stratt = "",
        numatt = 0,
        result = "win",
        //attempts = 3,
        duration = 3,
        date = System.currentTimeMillis(),
        difficulty = vm.instantGame.difficulty.value
    )
    var games: List<Game> = listOf(game, game)

    gameHistoryList = games
    MasterMindTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {
            //GameHistoryItemRow(gameHistory = game)
        }
    }
}