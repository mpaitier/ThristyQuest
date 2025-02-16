package com.example.thirstyquest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thirstyquest.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.MainMenu.name) {
        composable(Screen.MainMenu.name) { MainMenuScreen(navController) }
        composable(Screen.Profile.name) { ProfileScreen(navController) }
        composable(Screen.Social.name) { SocialScreen(navController) }
    }
}
