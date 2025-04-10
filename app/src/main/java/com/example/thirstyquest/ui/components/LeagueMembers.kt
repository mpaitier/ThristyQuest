package com.example.thirstyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getAllLeagueMembers
import com.example.thirstyquest.db.getLeagueOwnerId
import com.example.thirstyquest.db.getUserNameById
import com.example.thirstyquest.db.getUserXPById
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun LeagueMembersScreenContent(leagueID: String, navController: NavController, authViewModel: AuthViewModel) {
    var members by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }
    val userId by authViewModel.uid.observeAsState()
    var ownerID by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        ownerID = getLeagueOwnerId(leagueID)
        val memberList = getAllLeagueMembers(leagueID)
        val membersWithXP = mutableListOf<Pair<String, Double>>()

        memberList.forEach { uid ->
            getUserXPById(uid) { xp ->
                val memberXP = xp ?: 0.0
                membersWithXP.add(uid to memberXP)

                if (membersWithXP.size == memberList.size) {
                    members = membersWithXP.sortedByDescending { it.second }
                }
            }
        }
    }

    var position = 1
    Column (modifier = Modifier.fillMaxSize()) {
        // Title
        Text(
            text = stringResource(R.string.league_members) + " (" + members.size + ")",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // List of the league's members
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            members.forEach { member ->
                MemberItem(navController, userId.toString(), ownerID, member.first, position)
                position++
            }
        }
    }
}

// ------------------------------ League Member Item ------------------------------
@Composable
fun MemberItem(navController: NavController, currentUid: String, ownerId: String, uid: String, position: Int)
{
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var memberName by remember { mutableStateOf("") }
    var memberXP by remember { mutableDoubleStateOf(0.0) }
    var memberLevel by remember { mutableIntStateOf(0) }
    var photoUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        getUserNameById(uid) { name ->
            memberName = name ?: ""
        }

        getUserXPById(uid) { xp ->
            memberXP = xp ?: 0.0
        }
        memberLevel = (memberXP/2000).toInt()+1

        // Get profile picture
        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .await()
        photoUrl = snapshot.getString("photoUrl")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPressed) MaterialTheme.colorScheme.outlineVariant else Color.Transparent)
            .clickable (interactionSource = interactionSource, indication = null)
            {
                if(uid == currentUid) {
                    navController.navigate(Screen.Profile.name)
                } else {
                    navController.navigate(Screen.FriendProfile.name + "/$uid")
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!photoUrl.isNullOrEmpty()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Photo de profil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.pdp),
                contentDescription = "Profil",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                memberName,
                style = MaterialTheme.typography.bodyLarge,
                color = if(uid== currentUid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if(uid== currentUid) FontWeight.Bold else FontWeight.Normal,
            )
            Text(
                text = "Niveau $memberLevel",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (uid == ownerId) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Profil",
                modifier = Modifier.size(25.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
        }

        Text(
            "#${position}",
            modifier = Modifier.padding(2.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 20.sp
        )
    }
}
