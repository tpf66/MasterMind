package com.simoni.name.mastermind.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.simoni.name.mastermind.db.Game
import com.simoni.name.mastermind.model.MyViewModel
import kotlinx.coroutines.Dispatchers
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
            var selectedCount by remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                val history = withContext(Dispatchers.IO) {
                    vm.getAllGameHistory()
                }
                gameHistoryList = history.map { it.copy(isSelected = false) }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                LazyColumn {
                    items(gameHistoryList) { gameHistory ->
                        GameHistoryItemRow(
                            gameHistory = gameHistory,
                            gameViewModel = vm,
                            onSelectedChanged = { isSelected ->
                                if (isSelected) {
                                    selectedCount++
                                } else {
                                    selectedCount--
                                }
                            }
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Selected: $selectedCount",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)  // non cancellare!!
                    )

                    Button(
                        onClick = {
                            val selectedGames = gameHistoryList.filter { it.isSelected }
                            if (selectedGames.isNotEmpty()) {
                                runBlocking {
                                    vm.deleteSelectedGames(selectedGames)
                                }
                                gameHistoryList = gameHistoryList.filterNot { it.isSelected }
                                selectedCount = 0
                            }
                        }
                    ) {
                        Text(text = "Delete Selected")
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
    gameViewModel: MyViewModel,
    onSelectedChanged: (Boolean) -> Unit
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

            Checkbox(
                checked = gameHistory.isSelected,
                onCheckedChange = { isSelected ->
                    onSelectedChanged(isSelected)
                    gameHistory.isSelected = isSelected
                }
            )
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