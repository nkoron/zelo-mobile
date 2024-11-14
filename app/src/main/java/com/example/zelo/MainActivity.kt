package com.example.zelo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.zelo.ui.theme.ZeloTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZeloTheme {
                AppNavigation() // Calls the navigation setup
            }
        }
    }
}
