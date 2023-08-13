package com.simoni.name.mastermind.screen

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.simoni.name.mastermind.model.MyState
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.ui.theme.*

@Composable
fun Home(vm: MyViewModel) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
            ) {
                Text(text = "Mastermind")
                ButtonHome(vm,"prova") { vm.playHome() }
                ButtonHome(vm,"prova pi+ lunga") { vm.historyHome() }
            }
        }
        else -> {
        }
    }
}

@Composable
fun ButtonHome(
    vm : MyViewModel,
    text : String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Blue3),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(3.dp, Green),
        elevation =  ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp),
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