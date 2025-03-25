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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.R
import com.example.thirstyquest.data.User
import com.example.thirstyquest.data.members
import com.example.thirstyquest.navigation.Screen

@Composable
fun LeagueMembersScreenContent(leagueID: Int, navController: NavController) {
    // TODO : get members with leagueID

    // Sort members by level
    val sortedMembers = members.sortedByDescending { it.XP }

    var position = 1
    Column {
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
            sortedMembers.forEach { member ->
                MemberItem(navController, member, position)
                position++
            }
        }
    }
}

// ------------------------------ League Member Item ------------------------------
@Composable
fun MemberItem(navController: NavController, member: User, position: Int)
{
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val friendID = member.ID
    val ownerID = "12"
    // TODO : get league's owner ID
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPressed) MaterialTheme.colorScheme.outlineVariant else Color.Transparent)
            .clickable (interactionSource = interactionSource, indication = null)
            {
                navController.navigate(Screen.FriendProfile.name + "/$friendID")              // TODO : navigate to friend profile
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.pdp),
            contentDescription = "Profil",
            modifier = Modifier
                .size(40.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                member.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Niveau ${member.XP}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (member.ID == ownerID) {
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
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

//////////////////////////////////////////////////////////////////////////////////
//                               Previews

@Preview(showBackground = true)
@Composable
fun PreviewMemberItem() {
    MemberItem(navController = rememberNavController(), User("26","Alice", 10, true), 3)
    MemberItem(navController = rememberNavController(), User("12","Bob", 9, false), 4)
}