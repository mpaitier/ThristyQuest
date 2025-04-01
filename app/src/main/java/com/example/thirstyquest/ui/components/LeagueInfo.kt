package com.example.thirstyquest.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thirstyquest.R

@Composable
fun LeagueInfo(leagueID: String, onShareClick: (String) -> Unit) {
    // TODO : get informations with leagueID
    val leagueCode =  "ABCD"
    val currentLevel = 3
    val nextLevel = 4
    val currentXP = 900
    val requiredXP = 1000

    Column(
        modifier = Modifier.height(110.dp)
    ) {
        // League XP progress
        Text(
            text = stringResource(id = R.string.league_level),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))
        LeagueProgressBar(currentLevel, nextLevel, currentXP, requiredXP)
        Spacer(modifier = Modifier.height(18.dp))

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
            Spacer(modifier = Modifier.width(40.dp))
            // Share button
            IconButton(
                onClick = {
                    val leagueName = "Alcooliques anonymes"
                    val shareMessage =
                        "Viens rejoindre la ligue $leagueName sur Thirsty Quest et partageons nos aventures de consommation ! \uD83C\uDF7B\n" +
                                "Voici mon code de ligue : $leagueCode\n"
                    onShareClick(shareMessage)
                },
                modifier = Modifier.size(16.dp)
            ) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
            }
        }
    }
}
