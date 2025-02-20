package com.example.thirstyquest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    var showDialog by remember { mutableStateOf(false) }
    val currentBackStackEntry = navController.currentBackStackEntryAsState()

    CenterAlignedTopAppBar(
        title = {
            Text(
                modifier = Modifier.clickable {
                    navController.navigate(Screen.MainMenu.name)
                },
                text = stringResource(id = R.string.app_name),
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                color = primaryColor

            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigate(Screen.Profile.name) }) {
                Image(
                    painter = painterResource(id = R.drawable.pdp),
                    contentDescription = "Profil",
                    modifier = Modifier.size(60.dp)
                )
            }
        },
        actions = {
            if (currentBackStackEntry.value?.destination?.route == Screen.Profile.name) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Modifier"
                    )
                }
                IconButton(onClick = { navController.navigate(Screen.Settings.name) }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Paramètres"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
        )
    )

    if (showDialog) {
        ChangeProfilePictureDialog(
            onDismiss = { showDialog = false },
            onConfirm = {}
        )
    }
}







@Composable
fun ChangeProfilePictureDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Changer la photo de profil", fontWeight = FontWeight.Bold) },
        text = { Text("Souhaitez-vous changer votre photo de profil ?") },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Oui")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Non")
            }
        }
    )
}

@Preview
@Composable
fun PreviewTopBar() {
    TopBar(navController = rememberNavController())
}