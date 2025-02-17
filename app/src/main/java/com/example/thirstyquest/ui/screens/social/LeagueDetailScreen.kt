package com.example.thirstyquest.ui.screens.social

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LeagueDetailScreen(leagueID: Int, navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Détails de la Ligue $leagueID",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
