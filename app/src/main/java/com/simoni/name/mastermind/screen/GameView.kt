package com.simoni.name.mastermind.screen

import android.app.Activity
import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.foundation.*
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
import androidx.navigation.NavHostController
import com.simoni.name.mastermind.model.MyViewModel
import com.simoni.name.mastermind.model.OrientationUtils
import com.simoni.name.mastermind.model.utils.Attempt
import com.simoni.name.mastermind.model.utils.Difficulty
import com.simoni.name.mastermind.screen.gameobject.ButtonGuess
import com.simoni.name.mastermind.screen.gameobject.ColorSelection
import com.simoni.name.mastermind.screen.gameobject.EmptyCircle
import com.simoni.name.mastermind.screen.gameobject.FeedBack
import com.simoni.name.mastermind.screen.gameobject.Dialog
import com.simoni.name.mastermind.screen.gameobject.formatHour
import com.simoni.name.mastermind.screen.gameobject.OnFinish
import com.simoni.name.mastermind.ui.theme.*


@Composable
fun GameView(
    vm: MyViewModel,
    navController: NavHostController,
    dispatcher: OnBackPressedDispatcherOwner?,
    callback: OnBackPressedCallback,
    showDialog: MutableState<Boolean>,
    context: Context
) {
    dispatcher?.onBackPressedDispatcher?.addCallback(callback)
    callback.isEnabled= true
    OrientationUtils.lockOrientationPortrait(context as Activity)
    val selectedColors = remember { mutableStateListOf("X", "X", "X", "X", "X") }
    val clickable = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // button home and duration
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // button home
            Button(
                onClick = {
                    if (clickable.value) {
                        showDialog.value = true
                    }
                },
                colors = ButtonDefaults.buttonColors(Blue3),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(3.dp, Green),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 15.dp,
                    disabledElevation = 0.dp
                ),
                modifier = Modifier
                    .bounceClickEffect()
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = W
                )
            }
            Spacer(modifier = Modifier.weight(0.1f))

            // duration
            Text(
                text = formatHour(vm.instantGame.duration.longValue),
                color = W,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.weight(0.5f))

        // Game area with attempt
        GameArea(vm.instantGame.attempts, selectedColors)
        { i ->
            if (clickable.value) {
                selectedColors[i] = "X"
            }
        }
        Spacer(modifier = Modifier.weight(0.5f))

        // button with the color selection
        ColorSelection(vm)
        { color ->
            if (selectedColors.contains("X") && clickable.value) {
                for (i in 0 until selectedColors.size) {
                    if (selectedColors[i] == "X") {
                        if (vm.instantGame.difficulty.value == Difficulty.Easy && !selectedColors.contains(
                                color
                            )
                        )
                            selectedColors[i] = color
                        else if (vm.instantGame.difficulty.value == Difficulty.Normal)
                            selectedColors[i] = color
                        break
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(0.2f))

        // Button Submit
        ButtonGuess(selectedColors)
        { selectedColor ->
            if (!selectedColors.contains("X") && clickable.value) {
                val reset = listOf("X", "X", "X", "X", "X")
                vm.instantGame.attempt(selectedColor.joinToString(separator = ""))
                selectedColors.clear()
                selectedColors.addAll(reset)
            }
        }
    }
    // Dialog of end of the game
    if (vm.instantGame.isGameFinished.value) {
        OnFinish(vm, navController, clickable)
    } else {
        // make sure that you can't push the button while the banner is showed
        clickable.value = true
    }
    // Dialog of the backpress
    if (showDialog.value) {
        Dialog(vm, navController, showDialog)
    }
}














// game area
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
        // print the color for each row
        for (rowIndex in 0 until 10) {
            // choose the modifier for row
            val myModifier: Modifier = if (rowIndex == attempts.size) {
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(2.dp)
                    .border(BorderStroke(2.dp, Blue4), shape = RoundedCornerShape(15.dp))
            } else {
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(2.dp)
            }

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
                    // Draw each row
                    if (rowIndex < attempts.size) {
                        for (i in 0 until 5) {
                            EmptyCircle(attempts[rowIndex].guess[i].toString()) {}
                        }
                    } else if (rowIndex == attempts.size) {
                        for (i in 0 until 5)
                            EmptyCircle(selectedColors[i], onClick = { onClick(i) })
                    } else {
                        for (i in 0 until 5)
                            EmptyCircle(selectedColor = "X") {}
                    }
                }

                // draw the game hint
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
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




