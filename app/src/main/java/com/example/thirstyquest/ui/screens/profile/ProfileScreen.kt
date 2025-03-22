package com.example.thirstyquest.ui.screens.profile

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.thirstyquest.R
import kotlinx.coroutines.launch
import com.example.thirstyquest.ui.components.UserCollectionContent
import com.example.thirstyquest.ui.components.UserBadgesContent
import com.example.thirstyquest.ui.components.UserStatsContent
import com.example.thirstyquest.data.PublicationHist
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.viewmodel.AuthState
import com.example.thirstyquest.ui.viewmodel.AuthViewModel

//----------DATA--------

data class Badge(
    val name: String,
    val descriptions: List<String>,
    val currentLevel: Int,
    val maxLevel: Int,
    val progress: Int
)
val badgeList = listOf(
    Badge(
        name = "Connaisseur",
        descriptions = listOf("Goûter 5 boissons différentes", "Goûter 10 boissons différentes", "Goûter 20 boissons différentes"),
        currentLevel = 2,
        maxLevel = 3,
        progress = 11
    ),
    Badge(
        name = "Explorateur",
        descriptions = listOf("Découvrir 2 bars uniques", "Découvrir 5 bars uniques", "Découvrir 10 bars uniques"),
        currentLevel = 3,
        maxLevel = 3,
        progress = 12
    ),
    Badge(
        name = "Explorateur",
        descriptions = listOf("Découvrir 2 bars uniques", "Découvrir 5 bars uniques", "Découvrir 10 bars uniques"),
        currentLevel = 1,
        maxLevel = 3,
        progress = 3
    ),
    Badge(
            name = "Explorateur",
        descriptions = listOf("Boire 3 jours de la semaine", "Boire 5 jours de la semaine", "Boire 7 jours de la semaine"),
        currentLevel = 3,
        maxLevel = 3,
        progress = 7
    )
)

////////////////////////////////////////////////////////////////////////////////////////////////////
//                                      Composable

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val tabTitles = listOf(R.string.my_stats, R.string.my_collection,R.string.my_badge)
    val pagerState = rememberPagerState { tabTitles.size }
    val coroutineScope = rememberCoroutineScope()

    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate(Screen.Login.name)
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        pagerState.scrollToPage(1)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = { Text(stringResource(id = title)) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> UserStatsContent()
                1 -> UserCollectionContent()
                2 -> UserBadgesContent()
            }
        }
    }
}
