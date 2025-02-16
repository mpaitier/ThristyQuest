package com.example.thirstyquest.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.thirstyquest.navigation.Screen
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import com.example.thirstyquest.ui.theme.*

@Composable
fun BottomNavBar(navController: NavController) {
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) bottomNavBackgroundDark else bottomNavBackgroundLight
    val selectedColor = if (isDarkTheme) bottomNavSelectedDark else bottomNavSelectedLight
    val unselectedColor = if (isDarkTheme) bottomNavUnselectedDark else bottomNavUnselectedLight
    val selectedIconBackgroundColor = if (isDarkTheme) bottomNavIconBackgroundSelectedDark else bottomNavIconBackgroundSelectedLight

    val items = listOf(
        Screen.MainMenu to Icons.Filled.Home,
        Screen.Social to Icons.Filled.Person
    )

    NavigationBar(containerColor = backgroundColor) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { (screen, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = screen.name) },
                selected = currentRoute == screen.name,
                onClick = { navController.navigate(screen.name) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    indicatorColor = selectedIconBackgroundColor,
                )
            )
        }
    }
}

