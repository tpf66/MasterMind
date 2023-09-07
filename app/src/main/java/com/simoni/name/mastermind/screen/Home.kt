package com.simoni.name.mastermind.screen

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.simoni.name.mastermind.R
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.model.utils.Difficulty
import com.simoni.name.mastermind.ui.theme.Blue3
import com.simoni.name.mastermind.ui.theme.Green
import com.simoni.name.mastermind.ui.theme.W


@Composable
fun Home(vm: MyViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.5f))
                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 45.sp,
                    color = W,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(0.5f))
                HomeButton(text = stringResource(id = R.string.difficulty_easy)) {
                    navController.navigate("GameView")
                    vm.instantGame.difficulty.value = Difficulty.Easy
                    vm.newGame()
                }

                Spacer(modifier = Modifier.weight(0.1f))
                HomeButton(text = stringResource(id = R.string.difficulty_normal)) {
                    navController.navigate("GameView")
                    vm.instantGame.difficulty.value = Difficulty.Normal
                    vm.newGame()
                }

                Spacer(modifier = Modifier.weight(0.2f))
                HomeButton(
                    text = stringResource(id = R.string.game_history),
                    onClick = { navController.navigate("History") })

                Spacer(modifier = Modifier.weight(0.7f))
            }
        }

        else -> {
        }
    }
}


@Composable
fun HomeButton(
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Blue3),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(3.dp, Green),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        modifier = Modifier
            .height(55.dp)
            .width(200.dp)
            .bounceClickEffect()
    ) {
        Text(
            text = text,
            color = W
        )
    }
}

fun Modifier.bounceClickEffect() = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.70f else 1f)

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(isPressed) {
            awaitPointerEventScope {
                isPressed = if (isPressed) {
                    waitForUpOrCancellation()
                    false
                } else {
                    awaitFirstDown(false)
                    true
                }
            }
        }
}