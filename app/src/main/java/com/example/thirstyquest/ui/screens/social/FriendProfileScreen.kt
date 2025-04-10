package com.example.thirstyquest.ui.screens.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.example.thirstyquest.db.getFollowerCount
import com.example.thirstyquest.db.getFollowingCount
import com.example.thirstyquest.db.getFriendPublications
import com.example.thirstyquest.db.getUserNameById
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.AddFriendButton
import com.example.thirstyquest.ui.components.DrinkProgressBar
import com.example.thirstyquest.ui.dialog.FollowDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FriendProfileScreen(friendId: String, navController: NavController, authViewModel: AuthViewModel) {
    var showFriendsListDialog by remember { mutableStateOf(false) }
    var showPublicationsDialog by remember { mutableStateOf(false) }
    var showFullStatsDialog by remember { mutableStateOf(false) }
    var showFullBadgesDialog by remember { mutableStateOf(false) }

    var friendName by remember { mutableStateOf<String?>(null) }

    // Variables pour stocker les publications
    var publications by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var allPublications by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var showMorePublications by remember { mutableStateOf(false) }
    val displayedPublications = if (showMorePublications) allPublications else allPublications.take(3)

    var publiNumber by remember { mutableStateOf(0) }
    var followerNumber by remember { mutableStateOf(0) }
    var followingNumber by remember { mutableStateOf(0) }
    var photoUrl by remember { mutableStateOf<String?>(null) }


    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedPublication by remember { mutableStateOf<Pair<String, String>?>(null) }

    LaunchedEffect(friendId) {
        val fetchedPublications = getFriendPublications(friendId)
        publications = fetchedPublications.take(3)
        allPublications = fetchedPublications
        publiNumber = allPublications.size
        followerNumber = getFollowerCount(friendId)
        followingNumber = getFollowingCount(friendId)

        getUserNameById(friendId) { name ->
            friendName = name
        }

        // Get profile picture
        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .document(friendId)
            .get()
            .await()
        photoUrl = snapshot.getString("photoUrl")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
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
                text = friendName ?: "Nom non trouvé",  // Simplification du texte
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                displayedPublications.forEach { publication ->
                    // TODO : rendre les publi clickables
                    PublicationItem(
                        description = publication.first,
                        points = publication.second,
                        modifier = Modifier
                            .width(120.dp)
                            .height(100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Boutons + et -
            if (allPublications.size > 3 && !showMorePublications) {
                Button(
                    onClick = { showMorePublications = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("+")
                }
            } else if (showMorePublications) {
                Button(
                    onClick = { showMorePublications = false },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("-")
                }
            }
        }

        Text(
                text = "Statistiques",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Badges",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
    }



    if (showFriendsListDialog) {
        FollowDialog(uid = friendId, onDismiss = { showFriendsListDialog = false }, navController = navController, authViewModel = authViewModel)
    }
    // Suppression des dialogues commentés qui ne sont pas utilisés
    /*if (showPublicationsDialog) {
        FriendPublicationDialog(userID = friendId, onDismiss = { showPublicationsDialog = false })
    }

    if (showFullStatsDialog) {
        StatisticsDialog(userID = friendId, onDismiss = { showFullStatsDialog = false })
    }
    if (showFullBadgesDialog) {
        BadgeFriendDialog(userID = friendId, onDismiss = { showFullBadgesDialog = false })
    }*/
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//    Composable

@Composable
fun FriendProfileHeader(friendId: String, photoUrl:String, publiNumber: Int, followerNumber : Int, followingNumber: Int, onPublicationClick: () -> Unit , onFollowerClick: () -> Unit, authViewModel: AuthViewModel)
{
    var showImageFullscreen by remember { mutableStateOf(false) }

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
            ) {
                if (!photoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Photo de profil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { showImageFullscreen = true }, // Clic pour afficher,
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

            // Friend's count
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
            // DrinkProgressBar occupe une largeur fixe ou flexible selon l'approche
            DrinkProgressBar(
                currentXP = 50,
                maxXP = 100,
                modifier = Modifier.weight(1f) // Cela permet de donner à la barre une largeur proportionnelle
            )

            Spacer(modifier = Modifier.width(8.dp)) // Espace entre les éléments

            // Le bouton occupe son espace minimum
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




@Composable
fun PublicationItem(
    description: String,
    points: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Points: $points",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

