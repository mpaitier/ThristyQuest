package com.example.thirstyquest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
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
import androidx.compose.ui.graphics.Color
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
        EditProfileDialog(
            firstName = "Mathias",
            lastName = "Paitier",
            age = "21",
            onDismiss = { showDialog = false },
            onSave = { newFirstName, newLastName, newAge ->

                showDialog = false
            }
        )
    }
}

@Composable
fun EditProfileDialog(
    firstName: String,
    lastName: String,
    age: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var newFirstName by remember { mutableStateOf(firstName) }
    var newLastName by remember { mutableStateOf(lastName) }
    var newAge by remember { mutableStateOf(age) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Modifier le profil", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextButton(onClick = {}) {
                    Text("Changer la photo de profil")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newFirstName,
                    onValueChange = { newFirstName = it },
                    label = { Text("Prénom") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newLastName,
                    onValueChange = { newLastName = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newAge,
                    onValueChange = { newAge = it },
                    label = { Text("Âge") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(newFirstName, newLastName, newAge)
                onDismiss()
            }) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text("Annuler")
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun PreviewTopBar() {
    TopBar(navController = rememberNavController())
}
