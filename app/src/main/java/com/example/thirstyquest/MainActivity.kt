package com.example.thirstyquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.navigation.AppNavigation
import com.example.thirstyquest.ui.components.BottomNavBar
import com.example.thirstyquest.ui.components.TopBar
import com.example.thirstyquest.ui.theme.ThirstyQuestTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThirstyQuestTheme {
                ThirstyQuestApp()
            }
        }
    }
}


@Composable
fun ThirstyQuestApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomNavBar(navController) },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}





/*
@Composable
fun ThirstyQuestApp() {
    var isDarkMode by rememberSaveable { mutableStateOf(false) }

    MyAppTheme(darkTheme = isDarkMode) {
        val navController = rememberNavController()

        Scaffold(
            topBar = { TopBar(navController) },
            bottomBar = { BottomNavBar(navController) },
            contentWindowInsets = WindowInsets(0.dp)
        ) { innerPadding ->
            AppNavigation(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                isDarkMode = isDarkMode,
                onThemeChange = { isDarkMode = it }
            )
        }
    }
}

@Composable
fun MyAppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
        typography = Typography,
        content = content
    )
}*/