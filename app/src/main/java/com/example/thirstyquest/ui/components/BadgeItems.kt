package com.example.thirstyquest.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.thirstyquest.R
import com.example.thirstyquest.ui.dialog.BadgeDetailDialog
import com.example.thirstyquest.ui.screens.profile.Badge
import com.example.thirstyquest.ui.screens.profile.badgeList

@Composable
fun BadgeList() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(start = 20.dp, top = 15.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        items(badgeList) { badge ->
            BadgeItem(badge)
        }
    }
}

@Composable
fun BadgeItem(badge: Badge) {
    var showDialog by remember { mutableStateOf(false) }
    val badgeIcons = listOf(R.drawable.badge_bronze, R.drawable.badge_argent, R.drawable.badge_or)

    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(100.dp)
            .clickable { showDialog = true },
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = painterResource(id = badgeIcons[badge.currentLevel - 1]),
            contentDescription = badge.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    if (showDialog) {
        BadgeDetailDialog(onDismiss = {showDialog=false}, badge, badgeIcons)
    }
}