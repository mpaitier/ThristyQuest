package com.example.thirstyquest.ui.screens.social

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.core.content.ContextCompat.getDrawable
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.FriendsList
import com.example.thirstyquest.ui.components.LeagueList
import com.example.thirstyquest.ui.components.SearchBar
import com.example.thirstyquest.ui.components.SearchResultsList
import com.example.thirstyquest.ui.dialog.AddLeagueDialog
import com.example.thirstyquest.ui.dialog.CreateLeagueDialog
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SocialScreen(navController: NavController) {
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
            SearchResultsList(searchQuery)
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


@Composable
fun AddFriendButton(isFriend: Boolean, userName: String)
{                                                                                                   // TODO : userName -> userID
    // TODO : fix animation
    var isFriend by remember { mutableStateOf(isFriend) }
    var isAnimating by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        onClick = {
            focusManager.clearFocus()
            if (!isAnimating) {
                isAnimating = true
                coroutineScope.launch {
                    delay(1000)
                    isAnimating = false
                    isFriend = !isFriend
                    Toast.makeText(
                        context,
                        "$userName ${context.getString(if (isFriend) R.string.friend_added else R.string.friend_deleted)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        },
        modifier = Modifier.size(45.dp)
    ) {
        when {
            isAnimating && !isFriend -> {  // Filling animation
                Image(
                    painter = rememberDrawablePainter(
                        drawable = getDrawable(LocalContext.current, R.drawable.anim_beer_filling)
                    ),
                    contentDescription = "Filling beer",
                    modifier = Modifier.size(40.dp)
                )
            }

            isAnimating && isFriend -> {  // Emptying animation
                Image(
                    painter = rememberDrawablePainter(
                        drawable = getDrawable(LocalContext.current, R.drawable.anim_beer_emptying)
                    ),
                    contentDescription = "Emptying beer",
                    modifier = Modifier.size(40.dp)
                )
            }

            isFriend -> { // Full glass
                Image(
                    painter = painterResource(id = R.drawable.icon_beer_full),
                    contentDescription = "Beer full",
                    modifier = Modifier.size(40.dp)
                )
            }

            else -> {  // Empty glass
                Image(
                    painter = painterResource(id = R.drawable.icon_beer_empty),
                    contentDescription = "Beer empty",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////
//                               Previews

@PreviewScreenSizes
@Composable
fun SocialScreenPreview() {
    SocialScreen(rememberNavController())
}
