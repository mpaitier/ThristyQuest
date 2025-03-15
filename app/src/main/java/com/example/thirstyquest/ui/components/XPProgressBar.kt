package com.example.thirstyquest.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// - - - - - - - - - - - - - - - League  - - - - - - - - - - - - - - -
@Composable
fun LeagueProgressBar(currentLevel: Int, nextLevel: Int, currentXP: Int, requiredXP: Int) {
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

// - - - - - - - - - - - - - - - Drink  - - - - - - - - - - - - - - -
@Composable
fun DrinkProgressBar(currentXP: Int, maxXP: Int) {
    val progress = currentXP.toFloat() / maxXP.toFloat()
    val backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            color = tertiaryColor,
            trackColor = backgroundColor
        )

        Text(
            text = "$currentXP / $maxXP",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

//////////////////////////////////////////////////////////////////////////////////
//                               Previews

@Preview(showBackground = true)
@Composable
fun PreviewLeagueXPProgress() {
    LeagueProgressBar(currentLevel = 3, nextLevel = 4, currentXP = 600, requiredXP = 1000)
}