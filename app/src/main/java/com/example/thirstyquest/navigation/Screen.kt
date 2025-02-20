package com.example.thirstyquest.navigation

import androidx.annotation.StringRes
import com.example.thirstyquest.R

enum class Screen(@StringRes val title: Int) {
    MainMenu(title = R.string.app_name),
    Profile(title = R.string.profile),
    Social(title = R.string.social),
    Settings(title = R.string.settings),
    LeagueContent(title = R.string.league_content),
}
