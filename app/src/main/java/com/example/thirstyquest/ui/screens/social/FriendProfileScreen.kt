package com.example.thirstyquest.ui.screens.social

import android.util.Log
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getUserNameById
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.AddFriendButton
import com.example.thirstyquest.ui.components.DrinkProgressBar
import com.example.thirstyquest.ui.dialog.BadgeFriendDialog
import com.example.thirstyquest.ui.dialog.CollectionDialog
import com.example.thirstyquest.ui.dialog.FriendPublicationDialog
import com.example.thirstyquest.ui.dialog.FollowDialog
import com.example.thirstyquest.ui.dialog.StatisticsDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await



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

    LaunchedEffect(friendId) {
        val fetchedPublications = getFriendPublications(friendId)
        publications = fetchedPublications.take(3)
        allPublications = fetchedPublications

        getUserNameById(friendId) { name ->
            friendName = name
        }
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
            friendName ?: "",
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacement entre les publications
            ) {
                publications.take(3).forEach { publication ->
                    PublicationItem(publication.first, publication.second, modifier = Modifier.weight(1f))
                }
            }

            // Afficher le bouton "+" si on a plus de publications à afficher
            if (allPublications.size > 3 && !showMorePublications) {
                Button(
                    onClick = { showMorePublications = true },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "+")
                }
            }

            // Affichage des publications supplémentaires si nécessaire
            if (showMorePublications) {
                // Afficher les publications restantes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacement entre les publications
                ) {
                    allPublications.drop(3).forEach { publication ->
                        PublicationItem(publication.first, publication.second, modifier = Modifier.weight(1f))
                    }
                }

                // Bouton "-" pour masquer les publications supplémentaires
                Button(
                    onClick = { showMorePublications = false },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "-")
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

    // Suppression des dialogues commentés qui ne sont pas utilisés
    /*if (showPublicationsDialog) {
        FriendPublicationDialog(userID = friendId, onDismiss = { showPublicationsDialog = false })
    }
    if (showFriendsListDialog) {
        FollowDialog(uid = friendId, onDismiss = { showFriendsListDialog = false }, navController = navController, authViewModel = authViewModel)
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
fun FriendProfileHeader(friendId: String, friendName: String, onPublicationClick: () -> Unit , onFollowerClick: () -> Unit, authViewModel: AuthViewModel)
{
                                                                                                    // TODO : Get real values with userID
    // Information data
    val friendPublicationCount = 26
    val friendFollowersCount = 8
    val friendFollowingCount = 3


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


suspend fun getFriendPublications(friendId: String): List<Pair<String, String>> {
    val db = FirebaseFirestore.getInstance()
    val publications = mutableListOf<Pair<String, String>>()

    try {
        val result = db.collection("publications")
            .whereEqualTo("user_ID", friendId)
            .orderBy("date", Query.Direction.DESCENDING)
            .orderBy("hour", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()

        // Récupérer les publications sous forme de Paires (description, points)
        for (document in result) {
            val description = document.getString("description") ?: ""
            val points = document.getLong("points")?.toString() ?: "0"
            publications.add(Pair(description, points))
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Erreur de récupération des publications", e)
    }

    return publications
}

@Composable
fun PublicationItem(description: String, points: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                //maxLines = 2,
                overflow = TextOverflow.Ellipsis // Coupe le texte avec "..."
            )
            Text(
                text = "Points: $points",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
