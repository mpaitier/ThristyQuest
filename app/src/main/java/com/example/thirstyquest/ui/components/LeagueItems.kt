package com.example.thirstyquest.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getAllUserLeaguesIdCoroutine
import com.example.thirstyquest.db.getLeagueMemberCount
import com.example.thirstyquest.db.getLeagueName
import com.example.thirstyquest.db.getUserRank
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.viewmodel.AuthViewModel

/** In Social Screen */
@Composable
fun LeagueList(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var leagueList by remember { mutableStateOf<List<String>>(emptyList()) }
    val currentUserUid by authViewModel.uid.observeAsState()

    LaunchedEffect(currentUserUid) {
        currentUserUid?.let { uid ->
            leagueList = getAllUserLeaguesIdCoroutine(uid)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height((0.26f * LocalConfiguration.current.screenHeightDp).dp)
    ) {
        items(leagueList.size) { index ->
            LeagueItem(
                navController = navController,
                authViewModel = authViewModel,
                leagueID = leagueList[index]
            )
        }
    }
}


@Composable
fun LeagueItem(
    navController: NavController,
    authViewModel: AuthViewModel,
    leagueID: String
)
{
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val tertiaryColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)

    var peopleCount by remember { mutableStateOf("") }
    var leagueName by remember { mutableStateOf("") }
    LaunchedEffect(leagueID) {
        peopleCount = getLeagueMemberCount(leagueID)
        leagueName = getLeagueName(leagueID)
    }

    val currentUserUid by authViewModel.uid.observeAsState()
    var rank by remember { mutableStateOf("") }
    LaunchedEffect(currentUserUid) {
        currentUserUid?.let { uid ->
            rank = getUserRank(uid, leagueID).toString()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPressed) tertiaryColor else Color.Transparent)
            .clickable (
                interactionSource = interactionSource,
                indication = null
            ) {
                navController.navigate(Screen.LeagueContent.name + "/$leagueID")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: get league image------------------------------------------------------------------------------------
        Image(
            painter = painterResource(id = R.drawable.league_logo),
            contentDescription = "Image de ligue",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = leagueName,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = peopleCount,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Person Icon",
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = "$rank",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Star Icon",
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
