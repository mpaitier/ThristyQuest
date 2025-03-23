package com.example.thirstyquest.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thirstyquest.R
import com.example.thirstyquest.ui.screens.profile.Badge

@Composable
fun BadgeDetailDialog(
    onDismiss: () -> Unit,
    /** Badge containing all informations */
    badge: Badge,
    /** All badge's icons */
    badgeIcons: List<Int>
)
{
    val pagerState = rememberPagerState { 3 }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = badge.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Fermer",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalPager(state = pagerState) { page ->
                    val isUnlocked = page + 1 <= badge.currentLevel
                    Image(
                        painter = painterResource(id = badgeIcons[page]),
                        contentDescription = "Badge niveau ${page + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .alpha(if (isUnlocked) 1f else 0.3f),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { index ->
                        val color = if (index == pagerState.currentPage) Color.Black else Color.Gray
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .padding(6.dp)
                                .background(color, shape = CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = badge.descriptions[pagerState.currentPage],
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {},
        containerColor = Color.White
    )
}

@Composable
fun BadgeFriendDialog(userID: String, onDismiss: () -> Unit)
{                                                                                                   // TODO : set content
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.badge) + " de ami $userID" ) },
        text = {
            // Contenu
            Text("Contenu")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}