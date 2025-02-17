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

        HorizontalPager(
            count = 3, // Trois pages : Stats, Détails, Historique
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> LeagueStatsScreenContent(leagueID) // Page gauche
                1 -> LeagueMembersScreenContent(leagueID) // Page centrale
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

// ------------------------------ Bottom Dots ------------------------------
@Composable
fun BottomDots(pagerState: PagerState) {
    val pageCount = 3
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
