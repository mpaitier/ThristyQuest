package com.example.thirstyquest.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun LeagueDetailScreen(leagueID: Int, navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LeagueTopBar(navController, leagueID)
        Spacer(modifier = Modifier.height(16.dp))
        LeagueInfo(leagueID, onShareClick = { /* Share league code */ })
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ){
            MemberList()
        }
        BottomDots()
    }
}

//////////////////////////////////////////////////////////////////////////////////
//                                Composables
// ------------------------------ League Top Bar ------------------------------
@Composable
fun LeagueTopBar(navController: NavController, leagueID: Int) {
    val leagueName = "Ligue $leagueID"        // TODO : get league name with leagueID
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height((0.04166*LocalConfiguration.current.screenHeightDp).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(imageVector = Icons.Filled.Menu,       // TODO : replace by league picture
            contentDescription = "League picture",
            modifier = Modifier.size(60.dp))

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = leagueName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        // TODO : navigate to league settings & visible only if user is owner
        IconButton(onClick = { /* Modifier la ligue */ }) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Modifier")
        }
    }
}

// ------------------------------ League Info ------------------------------
@Composable
fun LeagueInfo(leagueID: Int, onShareClick: (String) -> Unit) {
    // TODO : get informations with leagueID
    val leagueCode =  "ABCD"
    val currentLevel = 3
    val nextLevel = 4
    val currentXP = 900
    val requiredXP = 1000

    Column (
        modifier = Modifier.height(126.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Code de ligue : ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append(leagueCode)
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(onClick = { onShareClick(leagueCode) }) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Niveau de la ligue",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        LeagueXPProgress(currentLevel, nextLevel, currentXP, requiredXP)
    }
}
// ------------------------------ League XP Progress ------------------------------
@Composable
fun LeagueXPProgress(currentLevel: Int, nextLevel: Int, currentXP: Int, requiredXP: Int) {
    val progress = currentXP.toFloat() / requiredXP

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Niv. $currentLevel", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Niv. $nextLevel", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text("$currentXP XP / $requiredXP XP", style = MaterialTheme.typography.bodyMedium)
    }
}

// ------------------------------ League Member List ------------------------------
@Composable
fun MemberList() {
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
            "Membres de la Ligue",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                sortedMembers.forEach { member ->
                    MemberItem(member, position)
                    position++
                }
            }
        }
    }
}

// ------------------------------ League Member Item ------------------------------
@Composable
fun MemberItem(member: Member, position: Int) {
    // TODO : get league's owner ID
    val ownerID = 12
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Filled.Person, contentDescription = "Profil", modifier = Modifier.size(40.dp))

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
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
        }

        Text(
            "#${position}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun BottomDots() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Premier point (petit)
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(MaterialTheme.colorScheme.secondary, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Point du milieu (plus grand)
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Troisième point (petit)
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(MaterialTheme.colorScheme.secondary, shape = CircleShape)
        )
    }
}


// Modèle de données pour un membre
data class Member(val ID: Int, val name: String, val level: Int)

//////////////////////////////////////////////////////////////////////////////////
//                               Previews

@Preview(showBackground = true)
@Composable
fun PreviewLeagueTopBar() {
    LeagueTopBar(navController = rememberNavController(), leagueID = 12)
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueInfo() {
    LeagueInfo(leagueID = 12, onShareClick = { /* Share league code */ })
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueXPProgress() {
    LeagueXPProgress(currentLevel = 3, nextLevel = 4, currentXP = 600, requiredXP = 1000)
}

@Preview(showBackground = true)
@Composable
fun PreviewMemberList() {
    MemberList()
}

@Preview(showBackground = true)
@Composable
fun PreviewMemberItem() {
    MemberItem(Member(26,"Alice", 10), 3)
    MemberItem(Member(12,"Bob", 9), 4)
}