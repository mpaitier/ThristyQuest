package com.example.thirstyquest.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.IconButton
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.FriendsList
import com.example.thirstyquest.ui.components.LeagueList
import com.example.thirstyquest.ui.components.SearchBar
import com.example.thirstyquest.ui.components.SearchResultsList
import com.example.thirstyquest.ui.dialog.AddLeagueDialog
import com.example.thirstyquest.ui.dialog.CreateLeagueDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel

@Composable
fun SocialScreen(navController: NavController, authViewModel: AuthViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var showCreateLeagueDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val friendNumber = 13                                                                           // TODO : replace with actual user's friends count
    val leagueNumber = 7                                                                            // TODO : replace with actual user's leagues count

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp, 8.dp, 16.dp, 4.dp)
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary

        SearchBar(searchQuery, onQueryChange = { searchQuery = it })

        Spacer(modifier = Modifier.height(12.dp))

        // If no user's entry, show user's league & friends
        if (searchQuery.isBlank()) {
            // League list
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.leagues) + " ($leagueNumber)",
                    fontSize = 20.sp,
                    color = primaryColor
                )

                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircleOutline,
                        contentDescription = "Ajouter une ligue",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LeagueList(navController, leagueNumber)
            // Friend list
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.friends) + " ($friendNumber)",
                fontSize = 20.sp,
                color = primaryColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            FriendsList(navController, friendNumber)
        }
        // If searching, show matching people
        else {
            SearchResultsList(searchQuery, navController, authViewModel)
        }
    }

    // Add league dialog
    if (showDialog) {
        AddLeagueDialog(
            onDismiss = { showDialog = false },
            onCreateLeague = {
                showDialog = false
                showCreateLeagueDialog = true
            },
            onJoinLeague = { leagueCode ->
                showDialog = false

                val newLeagueID = addToLeagueList(leagueCode)
                navController.navigate(Screen.LeagueContent.name + "/$newLeagueID")
            }
        )
    }

    // Create a league dialog
    if (showCreateLeagueDialog) {
        CreateLeagueDialog(
            onDismiss = { showCreateLeagueDialog = false },
            onValidate = { leagueName ->
                showCreateLeagueDialog = false

                val newLeagueID = createLeague(leagueName)
                navController.navigate(Screen.LeagueContent.name + "/$newLeagueID")
            }
        )
    }
}

fun createLeague(leagueName: String): Int
{
    // TODO : Create a new league with values
    return 69 // Random value
}

fun addToLeagueList(leagueCode: String): Int
{
    // TODO : Add league to user's list
    return 69 // Random value
}
