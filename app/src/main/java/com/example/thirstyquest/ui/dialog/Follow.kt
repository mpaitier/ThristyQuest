package com.example.thirstyquest.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getAllFollowersIdCoroutine
import com.example.thirstyquest.db.getAllFollowingIdCoroutine
import com.example.thirstyquest.db.getUserNameCoroutine
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.screens.social.FollowItem
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await


@Composable
fun FollowDialog(uid: String, onDismiss: () -> Unit, navController: NavController, authViewModel: AuthViewModel, defaultTabFollowers: Boolean)
{
    data class UserProfile(
        val id: String,
        val name: String,
        val photoUrl: String?
    )
    var showFollowers by remember { mutableStateOf(defaultTabFollowers) }
    var followersList by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var followingList by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    val userID = authViewModel.uid.observeAsState()
    val currentUserId = userID.value ?: ""

    LaunchedEffect(uid) {
        val followingDeferred = getAllFollowingIdCoroutine(uid).map { id ->
            async {
                val name = getUserNameCoroutine(id)
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(id)
                    .get()
                    .await()
                val photoUrl = snapshot.getString("photoUrl")
                UserProfile(id, name, photoUrl)
            }
        }
        followingList = followingDeferred.awaitAll()

        val followersDeferred = getAllFollowersIdCoroutine(uid).map { id ->
            async {
                val name = getUserNameCoroutine(id)
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(id)
                    .get()
                    .await()
                val photoUrl = snapshot.getString("photoUrl")
                UserProfile(id, name, photoUrl)
            }
        }
        followersList = followersDeferred.awaitAll()
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.contacts), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight/2)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showFollowers = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showFollowers) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(stringResource(R.string.followers))
                    }
                    Button(
                        onClick = { showFollowers = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showFollowers) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(stringResource(R.string.following))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(if (showFollowers) followersList else followingList) { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (user.id == currentUserId) {
                                        navController.navigate(Screen.Profile.name)
                                    } else {
                                        navController.navigate("${Screen.FriendProfile.name}/${user.id}")
                                    }                                },

                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = user.photoUrl,
                                    contentDescription = "Photo de profil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = user.name,
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

