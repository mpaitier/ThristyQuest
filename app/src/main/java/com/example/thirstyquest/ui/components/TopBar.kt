package com.example.thirstyquest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "ThirstyQuest",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigate(Screen.Profile.name) }) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Profil"
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewTopBar() {
    TopBar(navController = rememberNavController())
}
