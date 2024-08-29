package com.a1573595.parkingdemo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.a1573595.parkingdemo.ui.component.DoubleBackPress
import com.a1573595.parkingdemo.ui.navigation.NavGraph
import com.a1573595.parkingdemo.ui.theme.ParkingLotTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParkingLotTheme {
                DoubleBackPress()
                NavGraph()
            }
        }
    }
}