package com.example.thirstyquest.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.example.thirstyquest.R
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

import com.example.thirstyquest.ui.components.LeagueHistScreenContent
import com.example.thirstyquest.ui.components.LeagueMembersScreenContent
import com.example.thirstyquest.ui.components.LeagueStatsScreenContent

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LeagueContentScreen(leagueID: Int, navController: NavController) {
    val pagerState = rememberPagerState(
        initialPage = 1
    )

    val coroutineScope = rememberCoroutineScope()

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

        HorizontalPager(
            count = 3, // Trois pages : Stats, Détails, Historique
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> LeagueStatsScreenContent(leagueID) // Page gauche
                1 -> LeagueMembersScreenContent(navController, leagueID) // Page centrale
                2 -> LeagueHistScreenContent(leagueID) // Page droite
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BottomDots(pagerState)
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
        // League code & share button
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
        // League XP progress
        Text(
            text = stringResource(id = R.string.league_level),
            modifier = Modifier.align(Alignment.CenterHorizontally),
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
            Text(
                "Niv. $currentLevel",
                style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Niv. $nextLevel", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "$currentXP / $requiredXP XP",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// ------------------------------ Bottom Dots ------------------------------
@Composable
fun BottomDots(pagerState: PagerState) {
    val pageCount = 3
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            val color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primaryContainer
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}


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
