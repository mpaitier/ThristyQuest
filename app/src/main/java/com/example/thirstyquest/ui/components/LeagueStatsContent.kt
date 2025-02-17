package com.example.thirstyquest.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LeagueStatsScreenContent(leagueID: Int) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text ("Stats de la ligue $leagueID")
    }
}

//////////////////////////////////////////////////////////////////////////////////
//                                Composables
