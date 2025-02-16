package com.example.thirstyquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thirstyquest.ui.components.SearchBar
import com.example.thirstyquest.ui.theme.md_theme_light_primary

@Composable
fun SocialScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar en haut
        SearchBar()

        Spacer(modifier = Modifier.height(20.dp))

        // Texte "Social" centré
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Social", fontSize = 24.sp, color = md_theme_light_primary)
        }
    }
}
