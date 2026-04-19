package com.example.pract_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pract_1.navigation.AppNavHost
import com.example.pract_1.presentation.theme.Pract_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pract_1Theme {
                AppNavHost()
            }
        }
    }
}
