package com.simoni.name.mastermind.screen

import com.simoni.name.mastermind.ui.theme.W
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.model.utils.Difficulty
import com.simoni.name.mastermind.ui.theme.Blue3

@Composable
fun Settings(vm: MyViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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

            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = "Settings",
                color = W,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Difficulty:",
            color = W,
            fontSize = 25.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (vm.instantGame.difficulty.value == Difficulty.Easy),
                onClick = { vm.instantGame.difficulty.value = Difficulty.Easy }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = Difficulty.Easy.toString(),
                color = W,
                fontSize = 15.sp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (vm.instantGame.difficulty.value == Difficulty.Normal),
                onClick = { vm.instantGame.difficulty.value = Difficulty.Normal }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = Difficulty.Normal.toString(),
                color = W,
                fontSize = 15.sp
            )
        }
    }
}

