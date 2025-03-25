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
import com.example.thirstyquest.ui.viewmodel.AuthViewModel

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.name,
        modifier = modifier
    ) {
        composable(Screen.MainMenu.name)
        {
            MainMenuScreen(authViewModel)
        }


        composable(Screen.Social.name)
        {
            SocialScreen(navController, authViewModel)
        }
        composable(Screen.LeagueContent.name + "/{leagueId}")
        {
            backStackEntry ->
            val leagueID = backStackEntry.arguments?.getString("leagueId")?.toIntOrNull()
            if (leagueID != null) {
                LeagueContentScreen(leagueID = leagueID, navController = navController, authViewModel = authViewModel)
            }
            else {
                Log.e("Navigation", "leagueID is null")
            }
        }


        composable(Screen.Login.name)
        {
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.SignIn.name)
        {
            SignInScreen(navController, authViewModel)
        }
        composable(Screen.SignUp.name)
        {
            SignUpScreen(navController, authViewModel)
        }

        composable(Screen.Profile.name)
        {
            ProfileScreen(navController, authViewModel)
        }
        composable(Screen.Settings.name)
        {
            SettingsScreen(navController, authViewModel)
        }

        composable(Screen.FriendProfile.name + "/{userId}")
        {
                backStackEntry ->
            val userID = backStackEntry.arguments?.getString("userId")
            if (userID != null) {
                FriendProfileScreen(friendId = userID, navController = navController, authViewModel = authViewModel)
            }
            else {
                Log.e("Navigation", "leagueID is null")
            }
        }
    }
}

