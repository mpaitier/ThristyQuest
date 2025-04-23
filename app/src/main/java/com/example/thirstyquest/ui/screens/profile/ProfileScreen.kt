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
import com.example.thirstyquest.ui.components.UserStatsContent
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.UserPublicationsList
import com.example.thirstyquest.ui.viewmodel.AuthState
import com.example.thirstyquest.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val tabTitles = listOf(R.string.my_stats,R.string.my_publication,R.string.my_collection)
    val pagerState = rememberPagerState { tabTitles.size }
    val coroutineScope = rememberCoroutineScope()
    val uid = authViewModel.uid.observeAsState()
    val userId = uid.value ?: ""
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
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    modifier = Modifier.weight(1f),
                    text = { Text(stringResource(id = title)) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            when (page) {
                0 -> UserStatsContent(userId = userId)
                1 -> UserPublicationsList(authViewModel)
                2 -> UserCollectionContent(authViewModel)
            }
        }
    }
}
