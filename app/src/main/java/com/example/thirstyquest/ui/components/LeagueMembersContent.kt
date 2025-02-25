package com.example.thirstyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen

// Data model for a member
data class Member(val ID: Int, val name: String, val level: Int)

@Composable
fun LeagueMembersScreenContent(navController: NavController,leagueID: Int) {
    MemberList(navController = navController,)
}

// ------------------------------ League Member List ------------------------------
@Composable
fun MemberList(navController: NavController)
{
    // TODO : get members with leagueID
    val members = listOf(
        Member(26,"Mathias", 10),
        Member(12,"Romain", 38),
        Member(84,"Paul", 6),
        Member(2,"Goustan", 11),
        Member(1,"Dimitri", 7),
        Member(4,"Florent", 5),
        Member(18,"Dorian", 12),
        Member(14,"Killian", 9),
        Member(8,"Damien", 4),
        Member(74,"Mathis", 3),
        Member(51,"Maxime", 2),
        Member(42,"Vincent", 1),
        Member(28,"Titouan", 13),
        Member(47,"Antoine", 18),
        Member(1,"Alexandre", 100)
    )
    // Sort members by level
    val sortedMembers = members.sortedByDescending { it.level }

    var position = 1
    Column {
        Text(
            text = stringResource(R.string.league_members) + " (" + members.size + ")",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                sortedMembers.forEach { member ->
                    MemberItem(navController, member, position)
                    position++
                }
            }
        }
    }
}

// ------------------------------ League Member Item ------------------------------
@Composable
fun MemberItem(navController: NavController, member: Member, position: Int)
{
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val friendID = member.ID
    val ownerID = 12
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
                "Niveau ${member.level}",
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
fun PreviewMemberList() {
    MemberList(navController = rememberNavController(),)
}

@Preview(showBackground = true)
@Composable
fun PreviewMemberItem() {
    MemberItem(navController = rememberNavController(), Member(26,"Alice", 10), 3)
    MemberItem(navController = rememberNavController(), Member(12,"Bob", 9), 4)
}