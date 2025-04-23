package com.example.thirstyquest.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.thirstyquest.navigation.Screen
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.primary
    val selectedColor = MaterialTheme.colorScheme.tertiary
    val unselectedColor = MaterialTheme.colorScheme.primaryContainer
    val selectedIconBackgroundColor = MaterialTheme.colorScheme.primaryContainer

    val items = listOf(
        Screen.MainMenu to Icons.Filled.Home,
        Screen.Social to Icons.Filled.Person
    )
    NavigationBar(
        containerColor = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { (screen, icon) ->
            val isSelected = when (screen) {
                Screen.Social -> currentRoute == Screen.Social.name || currentRoute?.startsWith(Screen.LeagueContent.name) == true
                else -> currentRoute == screen.name
            }

            NavigationBarItem(
                icon = {
                    Icon(icon, contentDescription = screen.name)
                },
                selected = isSelected,
                onClick = {
                    if(!isSelected) {
                        navController.navigate(screen.name) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    indicatorColor = selectedIconBackgroundColor,
                )
            )
        }
    }
}
