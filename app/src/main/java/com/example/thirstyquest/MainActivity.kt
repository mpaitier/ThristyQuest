package com.example.thirstyquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.navigation.AppNavigation
import com.example.thirstyquest.ui.components.BottomNavBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            Scaffold(
                bottomBar = { BottomNavBar(navController) }
            ) { paddingValues ->
                Box(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
                    AppNavigation(navController)
                }
            }
        }
    }
}
