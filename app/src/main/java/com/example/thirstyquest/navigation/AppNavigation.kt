package com.example.thirstyquest.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.thirstyquest.ui.screens.MainMenuScreen
import com.example.thirstyquest.ui.screens.ProfileScreen
import com.example.thirstyquest.ui.screens.social.SocialScreen
import com.example.thirstyquest.ui.screens.social.LeagueContentScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.name,
        // startDestination = Screen.LeagueContent.name+"/3",  // to see changes faster
        modifier = modifier
    ) {
        composable(Screen.MainMenu.name)
        {
            MainMenuScreen(navController)
        }

        composable(Screen.Profile.name)
        {
            ProfileScreen(navController)
        }

        composable(Screen.Social.name)
        {
            SocialScreen(navController)
        }

        composable(Screen.LeagueContent.name + "/{leagueId}",) {
            backStackEntry ->
            val leagueID = backStackEntry.arguments?.getString("leagueId")?.toIntOrNull()
            if (leagueID != null) {
                LeagueContentScreen(leagueID = leagueID, navController = navController)
            }
            else {
                Log.e("Navigation", "leagueID is null")
            }
        }

    }
}

