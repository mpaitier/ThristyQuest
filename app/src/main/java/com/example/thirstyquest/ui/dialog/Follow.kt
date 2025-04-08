package com.example.thirstyquest.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getAllFollowersIdCoroutine
import com.example.thirstyquest.db.getAllFollowingIdCoroutine
import com.example.thirstyquest.db.getUserNameCoroutine
import com.example.thirstyquest.ui.screens.social.FollowItem
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll


@Composable
fun FollowDialog(uid: String, onDismiss: () -> Unit, navController: NavController, authViewModel: AuthViewModel)
{
    var showFollowers by remember { mutableStateOf(true) }
    var followersList by remember { mutableStateOf<List<String>>(emptyList()) }
    var followingList by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(uid){
        //followingList = getAllFollowingIdCoroutine(uid)
        //followersList = getAllFollowersIdCoroutine(uid)

        val followingNamesDeferred = getAllFollowingIdCoroutine(uid).map { id ->
            async { getUserNameCoroutine(id) }
        }
        followingList = followingNamesDeferred.awaitAll()

        // Récupérer les noms des utilisateurs pour chaque ID dans followersList
        val followersNamesDeferred = getAllFollowersIdCoroutine(uid).map { id ->
            async { getUserNameCoroutine(id) }
        }
        followersList = followersNamesDeferred.awaitAll()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.contacts), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Switch button between followers & following
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showFollowers = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showFollowers) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(stringResource(R.string.followers))
                    }
                    Button(
                        onClick = { showFollowers = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showFollowers) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(stringResource(R.string.following))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Followers / following content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(325.dp)
                ) {
                    if (showFollowers) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            followersList.forEach { follower ->
                                Text(
                                    text = follower,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            followingList.forEach { following ->
                                Text(
                                    text = following,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}


@Composable
fun FollowersPage(followersList: List<String>, navController: NavController, authViewModel: AuthViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(followersList.size) { index ->
            FollowItem(followersList[index], navController, authViewModel = authViewModel)
        }
    }
}

@Composable
fun FollowingPage(followingList: List<String>, navController: NavController,authViewModel: AuthViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(followingList.size) { index ->
            FollowItem(followingList[index], navController,authViewModel = authViewModel)
        }
    }
}

