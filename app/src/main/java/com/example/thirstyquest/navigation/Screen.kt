package com.example.thirstyquest.navigation

import androidx.annotation.StringRes
import com.example.thirstyquest.R

enum class Screen(@StringRes val title: Int) {
    MainMenu(title = R.string.app_name),
    Profile(title = R.string.profile),
    Social(title = R.string.social),
    Settings(title = R.string.settings),
    LeagueContent(title = R.string.league_content),
    Login(title = R.string.login),
    SignIn(title = R.string.sign_in),
    SignUp(title = R.string.sign_up)
}
