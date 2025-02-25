package com.example.thirstyquest.ui.screens.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen

@Composable
fun FriendProfileScreen(userID: Int, navController: NavController)
{
    var showFriendsListDialog by remember { mutableStateOf(false) }
    var showPublicationsDialog by remember { mutableStateOf(false) }

    var showFullCollectionDialog by remember { mutableStateOf(false) }
    var showFullStatsDialog by remember { mutableStateOf(false) }
    var showFullBadgesDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar with return button & friend name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Retour")
            }
            Text(
                text = "Ami $userID",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Friends information
        FriendProfileHeader(
            userID,
            onPublicationClick = {showPublicationsDialog = true},
            onFollowerClick = {showFriendsListDialog = true}
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Friend's collection
        FriendProfileCategoryHeader(sectionTitle = stringResource(R.string.collec), onClick = {showFullCollectionDialog = true})

        // Friend's stats
        FriendProfileCategoryHeader(sectionTitle = stringResource(R.string.stats), onClick = {showFullStatsDialog = true})

        // Friend's badges
        FriendProfileCategoryHeader(sectionTitle = stringResource(R.string.badge), onClick = {showFullBadgesDialog = true})
    }

    if (showPublicationsDialog) {
        PublicationDialog(userID = userID, onDismiss = { showPublicationsDialog = false })
    }

    if (showFriendsListDialog) {
        FollowersAndFollowingDialog(userID = userID, onDismiss = { showFriendsListDialog = false }, navController = navController)
    }

    if (showFullCollectionDialog) {
        CollectionDialog(userID = userID, onDismiss = { showFullCollectionDialog = false })
    }
    if (showFullStatsDialog) {
        StatsDialog(userID = userID, onDismiss = { showFullStatsDialog = false })
    }
    if (showFullBadgesDialog) {
        BadgesDialog(userID = userID, onDismiss = { showFullBadgesDialog = false })
    }
}

@Composable
fun FriendProfileHeader(userID: Int, onPublicationClick: () -> Unit , onFollowerClick: () -> Unit) {
    // For informations
    val friendPublicationCount = 26                                                                 // TODO : Get real values
    val friendFollowersCount = 8
    val friendFollowingCount = 3
    // Friend name
    val friendFirstName = "Romain"
    val friendLastName = "Croguennoc"
    // To add in friend
    val isFriend = true
    val userName = "Ami $userID"

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Profile picture
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pdp),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Friend's count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoStatItem(
                    title = stringResource(R.string.publi),
                    count = friendPublicationCount,
                    onClick = onPublicationClick
                )
                InfoStatItem(
                    title = stringResource(R.string.followers),
                    count = friendFollowersCount,
                    onClick = onFollowerClick
                )
                InfoStatItem(
                    title = stringResource(R.string.following),
                    count = friendFollowingCount,
                    onClick = onFollowerClick
                )
            }
        }

        Row (
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = friendFirstName)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = friendLastName)
            Spacer(modifier = Modifier.weight(1F))
            AddFriendButton(isFriend = isFriend, userName = userName)
        }

    }
}

@Composable
fun InfoStatItem(title: String, count: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "$count", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun FriendProfileCategoryHeader(sectionTitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = sectionTitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        TextButton(onClick = onClick) {
            Text(
                text = stringResource(R.string.see_more),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun FollowItem(user: Users, navController: NavController) {
    val currentUserID = 2                                                                           // TODO : get user's ID

    fun navigateToProfile() {
        val destination = if (user.ID == currentUserID) {
            Screen.Profile.name
        } else {
            Screen.FriendProfile.name + "/${user.ID}"
        }
        navController.navigate(destination)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.pdp),
            contentDescription = "Profil",
            modifier = Modifier
                .size(50.dp)
                .clickable { navigateToProfile() }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = user.name,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.clickable { navigateToProfile() }
        )

        Spacer(modifier = Modifier.weight(1F))

        AddFriendButton(isFriend = user.isFriend, userName = user.name)
    }
}



////////////////////////////////////////////////////////////////////////////////////////////////////
//                  Dialog

@Composable
fun PublicationDialog(userID: Int, onDismiss: () -> Unit) {
    // show userID's hist
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Historique des publications") },
        text = {
            Text("Affichage de l'historique des publications de l'utilisateur.")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}

@Composable
fun FollowersAndFollowingDialog(userID: Int, onDismiss: () -> Unit, navController: NavController) {
                                                                                                    // TODO : get real list with userID
    val followersList = listOf(
        Users(1, "Alice", 10, true),
        Users(2, "Bob", 8, false),
        Users(3, "Charlie", 12, true),
        Users(4, "Diana", 9, false),
        Users(5, "Ethan", 11, true),
        Users(6, "Fiona", 6, false),
        Users(7, "George", 14, true),
        Users(8, "Hannah", 10, false)
    )

    val followingList = listOf(
        Users(1, "Alice", 10, true),
        Users(4, "Diana", 9, false),
        Users(7, "George", 14, true)
    )

    var showFollowers by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Suivi & Abonnés", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Switch button
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
                        Text("Abonnés")
                    }
                    Button(
                        onClick = { showFollowers = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showFollowers) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Abonnements")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
fun FollowersPage(followersList: List<Users>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(followersList.size) { follower ->
            FollowItem(followersList[follower], navController = navController)
        }
    }
}

@Composable
fun FollowingPage(followingList: List<Users>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(followingList.size) { follower ->
            FollowItem(followingList[follower], navController = navController)
        }
    }
}

@Composable
fun CollectionDialog(userID: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.collec) + " de ami $userID" ) },
        text = {
            // Contenu
            Text("Contenu")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
fun StatsDialog(userID: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.stats) + " de ami $userID" ) },
        text = {
            // Contenu
            Text("Contenu")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
fun BadgesDialog(userID: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.badge) + " de ami $userID" ) },
        text = {
            // Contenu
            Text("Contenu")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}
