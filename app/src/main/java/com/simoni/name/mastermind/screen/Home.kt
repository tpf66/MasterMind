package com.simoni.name.mastermind.screen

import android.app.Activity
import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.simoni.name.mastermind.R
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.BuildConfig
import com.simoni.name.mastermind.model.OrientationUtils
import com.simoni.name.mastermind.model.utils.Difficulty
import com.simoni.name.mastermind.ui.theme.Black
import com.simoni.name.mastermind.ui.theme.Blue1
import com.simoni.name.mastermind.ui.theme.Blue2
import com.simoni.name.mastermind.ui.theme.Blue3
import com.simoni.name.mastermind.ui.theme.Green
import com.simoni.name.mastermind.ui.theme.W


@Composable
fun Home(
    vm: MyViewModel,
    navController: NavHostController,
    context: Context,
    callback: OnBackPressedCallback
) {
    OrientationUtils.lockOrientationPortrait(context as Activity)

    callback.isEnabled = false
    BackHandler(onBack = { })

    val dialog = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Button info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .bounceClickEffect(),
                onClick = {
                    dialog.value = true
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
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = W
                )
            }
        }
        Spacer(modifier = Modifier.weight(0.5f))

        // Title
        Text(
            text = stringResource(id = R.string.app_name),
            modifier = Modifier.padding(bottom = 16.dp),
            fontSize = 45.sp,
            color = W,
            fontWeight = FontWeight.Bold
        )

        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp) // size of image
                .align(Alignment.CenterHorizontally)
        )

        // Button game easy
        HomeButton(text = stringResource(id = R.string.difficulty_easy)) {
            navController.navigate("GameView")
            vm.instantGame.difficulty.value = Difficulty.Easy
            vm.newGame()
        }
        Spacer(modifier = Modifier.weight(0.1f))

        // Button game normal
        HomeButton(text = stringResource(id = R.string.difficulty_normal)) {
            navController.navigate("GameView")
            vm.instantGame.difficulty.value = Difficulty.Normal
            vm.newGame()
        }
        Spacer(modifier = Modifier.weight(0.5f))

        // Botton nagivate to history
        HomeButton(text = stringResource(id = R.string.game_history)){
             navController.navigate("History")
        }
        Spacer(modifier = Modifier.weight(0.7f))

        // App version
        Text(
            text = stringResource(id = R.string.version) + BuildConfig.VERSION_NAME,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End),
            color = W,
            fontSize = 10.sp,
        )
    }
    // Show tutorial dialog
    if (dialog.value) {
        Tutorial(dialog)
    }
}


// Composable function of tutorial
@Composable
fun Tutorial(dialog: MutableState<Boolean>) {
    Dialog(onDismissRequest = { dialog.value = false }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            colors = CardDefaults.cardColors(containerColor = W),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Title
                Text(
                    text = stringResource(R.string.tutorial),
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Start,
                    color = Black,
                    fontSize = 25.sp
                )
                // Tutorial
                Text(
                    text = stringResource(id = R.string.how_to_play_instruction),
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                        .verticalScroll(rememberScrollState())
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                    textAlign = TextAlign.Start,
                    color = Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}


// Button
@Composable
fun HomeButton(
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Blue2),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(3.dp, Blue1),
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


/*@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val dialog = remember { mutableStateOf(false) }

    Tutorial(dialog = dialog)
}*/


// Bounce effect of the buttons
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