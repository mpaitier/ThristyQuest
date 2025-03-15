package com.example.thirstyquest.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thirstyquest.ui.screens.profile.LoginScreen
import com.example.thirstyquest.ui.screens.MainMenuScreen
import com.example.thirstyquest.ui.screens.profile.ProfileScreen
import com.example.thirstyquest.ui.screens.social.SocialScreen
import com.example.thirstyquest.ui.screens.social.LeagueContentScreen
import com.example.thirstyquest.ui.screens.profile.SettingsScreen
import com.example.thirstyquest.ui.screens.profile.SignInScreen
import com.example.thirstyquest.ui.screens.profile.SignUpScreen
import com.example.thirstyquest.ui.screens.social.FriendProfileScreen

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
            MainMenuScreen()
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


        composable(Screen.Login.name){
            LoginScreen(navController)
        }
        composable(Screen.SignIn.name) {
            SignInScreen(navController)
        }
        composable(Screen.SignUp.name) {
            SignUpScreen(navController)
        }

        composable(Screen.Profile.name)
        {
            ProfileScreen()
        }
        composable(Screen.Settings.name)
        {
            SettingsScreen(navController)
        }

        composable(Screen.FriendProfile.name + "/{userId}",) {
                backStackEntry ->
            val userID = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            if (userID != null) {
                FriendProfileScreen(userID = userID, navController = navController)
            }
            else {
                Log.e("Navigation", "leagueID is null")
            }
        }


    }
}

