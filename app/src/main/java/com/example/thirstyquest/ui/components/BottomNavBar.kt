package com.example.thirstyquest.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Face
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.thirstyquest.navigation.Screen

@Composable
fun BottomNavBar(navController: NavController) {
    // Define the items in the bottom navigation bar
    val items = listOf(
        Screen.Profile to Icons.Filled.Person,      // Move to the topbar
        Screen.MainMenu to Icons.Filled.Home,
        Screen.Social to Icons.Filled.Face,
    )

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { (screen, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = screen.name) },
                selected = currentRoute == screen.name,
                onClick = { navController.navigate(screen.name) }
            )
        }
    }
}
