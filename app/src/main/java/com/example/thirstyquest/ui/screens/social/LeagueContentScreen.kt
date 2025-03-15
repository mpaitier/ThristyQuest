package com.example.thirstyquest.ui.screens.social

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

import com.example.thirstyquest.ui.components.LeagueHistScreenContent
import com.example.thirstyquest.ui.components.LeagueInfo
import com.example.thirstyquest.ui.components.LeagueMembersScreenContent
import com.example.thirstyquest.ui.components.LeagueStatsScreenContent
import com.example.thirstyquest.ui.components.LeagueTopBar

@Composable
fun LeagueContentScreen(leagueID: Int, navController: NavController) {
    val pagerState = rememberPagerState(initialPage = 1)

    val context = LocalContext.current  // for share intent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar
        LeagueTopBar(navController, leagueID)
        Spacer(modifier = Modifier.height(16.dp))
        LeagueInfo(
            leagueID,
            onShareClick = { shareMessage ->
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                    type = "text/plain"
                }

                val chooserIntent = Intent.createChooser(shareIntent, "Partager via")
                context.startActivity(chooserIntent)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> LeagueStatsScreenContent(leagueID)                     // Left
                1 -> LeagueMembersScreenContent(leagueID, navController)    // Center
                2 -> LeagueHistScreenContent(leagueID, navController)       // Right
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BottomDots(pagerState)
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
