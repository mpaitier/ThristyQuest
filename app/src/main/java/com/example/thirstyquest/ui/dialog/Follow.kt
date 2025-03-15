package com.example.thirstyquest.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.thirstyquest.data.User
import com.example.thirstyquest.ui.screens.social.FollowItem
import com.example.thirstyquest.data.followersList
import com.example.thirstyquest.data.followingList

@Composable
fun FollowDialog(userID: Int, onDismiss: () -> Unit, navController: NavController)
{
    // TODO : get real list with userID


    var showFollowers by remember { mutableStateOf(true) }

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
                        FollowersPage(followersList, navController)
                    } else {
                        FollowingPage(followingList, navController)
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
fun FollowersPage(followersList: List<User>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(followersList.size) { follower ->
            FollowItem(followersList[follower], navController = navController)
        }
    }
}

@Composable
fun FollowingPage(followingList: List<User>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(followingList.size) { follower ->
            FollowItem(followingList[follower], navController = navController)
        }
    }
}