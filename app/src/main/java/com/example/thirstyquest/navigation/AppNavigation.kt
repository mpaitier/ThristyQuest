package com.example.thirstyquest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thirstyquest.ui.screens.MainMenuScreen
import com.example.thirstyquest.ui.screens.ProfileScreen
import com.example.thirstyquest.ui.screens.SocialScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.name,
        modifier = modifier // Appliquer le padding ici
    ) {
        composable(Screen.MainMenu.name) { MainMenuScreen(navController) }
        composable(Screen.Profile.name) { ProfileScreen(navController) }
        composable(Screen.Social.name) { SocialScreen(navController) }
    }
}

