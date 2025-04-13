package com.example.thirstyquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.navigation.AppNavigation
import com.example.thirstyquest.ui.components.BottomNavBar
import com.example.thirstyquest.ui.components.TopBar
import com.example.thirstyquest.ui.theme.ThirstyQuestTheme
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.example.thirstyquest.db.DrinkPointManager
import com.example.thirstyquest.ui.viewmodel.SettingsViewModel
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authViewModel: AuthViewModel by viewModels()
        val settingsViewModel: SettingsViewModel by viewModels()

        DrinkPointManager.checkAndUpdateDrinkPointsIfNeeded()

        setContent {
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

            ThirstyQuestTheme(darkTheme = isDarkMode) {
                ThirstyQuestApp(
                    authViewModel = authViewModel,
                    settingsViewModel = settingsViewModel,
                    isDarkMode = isDarkMode
                )
            }
        }

    }
}


@Composable
fun ThirstyQuestApp(authViewModel: AuthViewModel, settingsViewModel: SettingsViewModel, isDarkMode: Boolean) {
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopBar(navController, authViewModel = authViewModel) },
        bottomBar = { BottomNavBar(navController) },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            authViewModel = authViewModel,
            settingsViewModel = settingsViewModel
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