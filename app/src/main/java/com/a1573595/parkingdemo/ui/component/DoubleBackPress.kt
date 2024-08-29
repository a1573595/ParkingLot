package com.a1573595.parkingdemo.ui.component

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.a1573595.parkingdemo.R

import kotlinx.coroutines.delay

sealed class BackPress {
    data object Idle : BackPress()
    data object InitialTouch : BackPress()
}

@Composable
fun DoubleBackPress() {
    var showToast by remember { mutableStateOf(false) }
    var backPressState by remember { mutableStateOf<BackPress>(BackPress.Idle) }

    val context = LocalContext.current

    if(showToast){
        Toast.makeText(context, stringResource(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
        showToast= false
    }

    LaunchedEffect(key1 = backPressState) {
        if (backPressState == BackPress.InitialTouch) {
            delay(2000)
            backPressState = BackPress.Idle
        }
    }

    BackHandler(backPressState == BackPress.Idle) {
        backPressState = BackPress.InitialTouch
        showToast = true
    }
}