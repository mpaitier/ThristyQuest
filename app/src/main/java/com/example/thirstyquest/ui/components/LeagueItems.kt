package com.example.thirstyquest.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen

/** In Social Screen */
@Composable
fun LeagueList(
    navController: NavController,
    leagueNumber: Int
)
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((0.26f* LocalConfiguration.current.screenHeightDp).dp)
            .verticalScroll(rememberScrollState()) // Enable manual scroll
    ) {
        Column {
            repeat(leagueNumber) { index ->
                LeagueItem(
                    navController = navController,
                    leagueID = index    // TODO : get the league ID
                )
            }
        }
    }
}

@Composable
fun LeagueItem(
    navController: NavController,
    leagueID: Int
)
{
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val tertiaryColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)

    val peopleCount = 26                    // TODO : get the number of people in the league with the leagueID
    val rank = 8                            // TODO : get the rank of the user in the league with the leagueID
    val leagueName = "Ligue $leagueID"      // TODO : get the name of the league with the leagueID

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
            text = "$peopleCount",
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

//////////////////////////////////////////////////////////////////////////////////
//                               Previews

@Preview
@Composable
fun LeagueItemPreview() {
    LeagueItem(navController = rememberNavController(), 26)
}