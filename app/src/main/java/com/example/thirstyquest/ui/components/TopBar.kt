package com.example.thirstyquest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.R

import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {

    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background

    var searchQuery by remember { mutableStateOf("") }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                color = primaryColor
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigate(Screen.Profile.name) }) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Profil"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
        )

    )
}

@Preview
@Composable
fun PreviewTopBar() {
    TopBar(navController = rememberNavController())
}
