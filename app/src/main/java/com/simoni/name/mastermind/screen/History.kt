package com.simoni.name.mastermind.screen

import android.content.res.Configuration
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

@Composable
fun History(vm: MyViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            var gameHistoryList by remember { mutableStateOf<List<Game>>(emptyList()) }
            val stateLazy = rememberLazyListState()

            LaunchedEffect(Unit) {
                gameHistoryList = withContext(Dispatchers.IO) {
                    vm.getAllGameHistory()
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    Button(
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

                if (gameHistoryList.isEmpty()){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "Empty history",
                            color = W,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }else {
                    LazyColumn(
                        modifier = Modifier,
                        userScrollEnabled = true,
                        state = stateLazy
                    ) {
                        itemsIndexed(gameHistoryList) { index, it ->

                            GameHistoryItemRow(gameHistory = it)
                            { gameToDelete ->
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
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "ID: ${gameHistory.id}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Versione App: ${gameHistory.version}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Codice Segreto: ${gameHistory.secretCode}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Risultato: ${gameHistory.result}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Tentativi: ${gameHistory.attempts}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Durata Partita: ${gameHistory.duration} ms", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Data Partita: ${formatDate(gameHistory.date)}", fontSize = 16.sp)
            }

            Button(
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
            History(vm = vm, navController = navController)
        }
    }
}