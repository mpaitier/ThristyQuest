package com.example.thirstyquest.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// - - - - - - - - - - - - - - - League  - - - - - - - - - - - - - - -
@Composable
fun LeagueProgressBar(currentLevel: Int, currentXP: Double, requiredXP: Int) {
    val newCurrentXP = (currentXP%requiredXP).toInt()
    val progress = newCurrentXP.toFloat() / requiredXP

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
            Text("Niv. ${currentLevel+1}", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "$newCurrentXP / $requiredXP XP",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// - - - - - - - - - - - - - - - Drink & User  - - - - - - - - - - - - - - -
@Composable
fun ProgressBar(
    currentLevel: Int,
    currentXP: Int,
    requiredXP: Int,
    modifier: Modifier = Modifier
) {
    val progress = currentXP.toFloat() / requiredXP.toFloat()
    val backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "Niv. $currentLevel",
            style = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                trackColor = backgroundColor
            )

            Text(
                text = "$currentXP / $requiredXP XP",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        Text(
            text = "Niv. ${currentLevel + 1}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
