package com.example.thirstyquest.ui.screens.social

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thirstyquest.R
import com.example.thirstyquest.data.Category
import com.example.thirstyquest.data.Publication
import com.example.thirstyquest.db.calculateLevelAndRequiredXP
import com.example.thirstyquest.db.getCollectionUser
import com.example.thirstyquest.db.getFollowerCount
import com.example.thirstyquest.db.getFollowingCount
import com.example.thirstyquest.db.getFriendPublications
import com.example.thirstyquest.db.getUserLastPublications
import com.example.thirstyquest.db.getUserNameById
import com.example.thirstyquest.db.getUserXPById
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.AddFriendButton
import com.example.thirstyquest.ui.components.FriendPublicationItem
import com.example.thirstyquest.ui.components.FriendPublications
import com.example.thirstyquest.ui.components.ProgressBar
import com.example.thirstyquest.ui.components.PublicationItem
import com.example.thirstyquest.ui.components.SortButton
import com.example.thirstyquest.ui.components.UserPublications
import com.example.thirstyquest.ui.components.UserStatsContent
import com.example.thirstyquest.ui.dialog.DrinkItem
import com.example.thirstyquest.ui.dialog.FollowDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FriendProfileScreen(friendId: String, navController: NavController, authViewModel: AuthViewModel) {
    var showFriendsListDialog by remember { mutableStateOf(false) }
    var showPublicationsDialog by remember { mutableStateOf(false) }
    var showMoreCollection by remember { mutableStateOf(false) }

    var friendName by remember { mutableStateOf<String?>(null) }

    var publiNumber by remember { mutableStateOf(0) }
    var followerNumber by remember { mutableStateOf(0) }
    var followingNumber by remember { mutableStateOf(0) }
    var photoUrl by remember { mutableStateOf<String?>(null) }
    var fullList by remember { mutableStateOf<List<Category>>(emptyList()) }
    val visibleItems = if (showMoreCollection) fullList else fullList.take(3)


    LaunchedEffect(friendId) {
        getUserLastPublications(friendId) { newList ->
            publiNumber = newList.size
        }
        followerNumber = getFollowerCount(friendId)
        followingNumber = getFollowingCount(friendId)

        getUserNameById(friendId) { name ->
            friendName = name
        }

        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .document(friendId)
            .get()
            .await()
        photoUrl = snapshot.getString("photoUrl")


        fullList = getCollectionUser(friendId)

    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item {
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
                    text = friendName ?: "Nom non trouvé",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            FriendProfileHeader(
                friendId,
                photoUrl.toString(),
                publiNumber,
                followerNumber,
                followingNumber,
                onPublicationClick = { showPublicationsDialog = true },
                onFollowerClick = { showFriendsListDialog = true },
                authViewModel
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Publications récentes",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            FriendPublications(friendId)

            Text(
                text = "Collection",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = 500.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                items(visibleItems) { drink ->
                    val icon = when (drink.name) {
                        "Bière" -> painterResource(id = R.drawable.biere)
                        "Vin" -> painterResource(id = R.drawable.vin)
                        "Cocktail" -> painterResource(id = R.drawable.cocktail)
                        "Shot" -> painterResource(id = R.drawable.shot)
                        else -> painterResource(id = R.drawable.other)
                    }
                    DrinkItem(userId = friendId,drink = drink, icon = icon)
                }
            }
            if (fullList.size > 3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { showMoreCollection = !showMoreCollection }) {
                        Text(if (showMoreCollection) "-" else "+")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Statistiques",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            UserStatsContent(authViewModel, userId = friendId, isFriend = true)
        }
    }



    if (showFriendsListDialog) {
        FollowDialog(uid = friendId, onDismiss = { showFriendsListDialog = false }, navController = navController, authViewModel = authViewModel)
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//    Composable

@Composable
fun FriendProfileHeader(friendId: String, photoUrl:String, publiNumber: Int, followerNumber : Int, followingNumber: Int, onPublicationClick: () -> Unit , onFollowerClick: () -> Unit, authViewModel: AuthViewModel)
{
    var showImageFullscreen by remember { mutableStateOf(false) }
    var userXP by remember { mutableStateOf(0.0) }
    var currentLevel by remember { mutableStateOf(1) }
    var requiredXP by remember { mutableStateOf(0) }

    LaunchedEffect(friendId) {
        getUserXPById(friendId) { xp ->
            userXP = xp ?: 0.0
            val (lvl, reqXP) = calculateLevelAndRequiredXP(xp?: 0.0)
            currentLevel = lvl
            requiredXP = reqXP
        }
    }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            ) {
                if (!photoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Photo de profil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { showImageFullscreen = true },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.pdp),
                        contentDescription = "Profil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoFriendStatItem(
                    title = stringResource(R.string.publi),
                    count = publiNumber,
                    onClick = onPublicationClick
                )
                InfoFriendStatItem(
                    title = stringResource(R.string.followers),
                    count = followerNumber,
                    onClick = onFollowerClick
                )
                InfoFriendStatItem(
                    title = stringResource(R.string.following),
                    count = followingNumber,
                    onClick = onFollowerClick
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressBar(
                currentLevel = currentLevel,
                currentXP = (userXP % requiredXP).toInt(),
                requiredXP = requiredXP
            )


            Spacer(modifier = Modifier.width(8.dp))

            AddFriendButton(friendId = friendId, authViewModel = authViewModel)
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
fun FollowItem(uid:String, navController: NavController, authViewModel: AuthViewModel)
{
    var userName by remember { mutableStateOf<String?>(null) }

    // Fetch the user name based on the uid
    LaunchedEffect(uid) {
        getUserNameById(uid) { name ->
            userName = name
        }
    }

    fun navigateToProfile() {
        val destination = if (uid == authViewModel.uid.toString()) {
            Screen.Profile.name
        } else {
            Screen.FriendProfile.name + "/${uid}"
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
            text = userName?:"",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.clickable { navigateToProfile() }
        )
        Spacer(modifier = Modifier.weight(1F))
        // Friend button
        AddFriendButton(uid, authViewModel)
    }
}


