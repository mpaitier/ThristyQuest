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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.data.User
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.AddFriendButton
import com.example.thirstyquest.ui.dialog.BadgeFriendDialog
import com.example.thirstyquest.ui.dialog.CollectionDialog
import com.example.thirstyquest.ui.dialog.FriendPublicationDialog
import com.example.thirstyquest.ui.dialog.FollowDialog
import com.example.thirstyquest.ui.dialog.StatisticsDialog

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
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
        FriendPublicationDialog(userID = userID, onDismiss = { showPublicationsDialog = false })
    }
    if (showFriendsListDialog) {
        FollowDialog(userID = userID, onDismiss = { showFriendsListDialog = false }, navController = navController)
    }
    if (showFullCollectionDialog) {
        CollectionDialog(userID = userID, onDismiss = { showFullCollectionDialog = false })
    }
    if (showFullStatsDialog) {
        StatisticsDialog(userID = userID, onDismiss = { showFullStatsDialog = false })
    }
    if (showFullBadgesDialog) {
        BadgeFriendDialog(userID = userID, onDismiss = { showFullBadgesDialog = false })
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//    Composable

@Composable
fun FriendProfileHeader(userID: Int, onPublicationClick: () -> Unit , onFollowerClick: () -> Unit)
{
                                                                                                    // TODO : Get real values with userID
    // Information data
    val friendPublicationCount = 26
    val friendFollowersCount = 8
    val friendFollowingCount = 3
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
                InfoFriendStatItem(
                    title = stringResource(R.string.publi),
                    count = friendPublicationCount,
                    onClick = onPublicationClick
                )
                InfoFriendStatItem(
                    title = stringResource(R.string.followers),
                    count = friendFollowersCount,
                    onClick = onFollowerClick
                )
                InfoFriendStatItem(
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
            // TODO : Change username with something else (already used in top bar)
            Text(text = userName)
            Spacer(modifier = Modifier.weight(1F))
            AddFriendButton(isFriend = isFriend, userName = userName)
        }
    }
}

@Composable
fun InfoFriendStatItem(title: String, count: Int, onClick: () -> Unit)
{
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
fun FriendProfileCategoryHeader(sectionTitle: String, onClick: () -> Unit)
{
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
fun FollowItem(user: User, navController: NavController)
{
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
        // Profile picture
        Image(
            painter = painterResource(id = R.drawable.pdp),
            contentDescription = "Profile",
            modifier = Modifier
                .size(50.dp)
                .clickable { navigateToProfile() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Username
        Text(
            text = user.name,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.clickable { navigateToProfile() }
        )
        Spacer(modifier = Modifier.weight(1F))
        // Friend button
        AddFriendButton(isFriend = user.isFriend, userName = user.name)
    }
}
