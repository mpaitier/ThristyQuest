package com.example.thirstyquest.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getLeagueName
import com.example.thirstyquest.db.getLeagueXp

@Composable
fun LeagueInfo(leagueID: String, onShareClick: (String) -> Unit)
{
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var currentXP by remember { mutableDoubleStateOf(0.0) }
    var currentLevel by remember { mutableIntStateOf(0) }
    var leagueName by remember { mutableStateOf("") }
    val requiredXP = 2000
    LaunchedEffect(Unit) {
        currentXP = getLeagueXp(leagueID)
        currentLevel =  (currentXP/requiredXP).toInt()+1
        leagueName = getLeagueName(leagueID)
    }

    Column(
        modifier = Modifier.height(110.dp)
    ) {
        // ----------------- League XP progress -----------------
        Text(
            text = stringResource(id = R.string.league_level),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        LeagueProgressBar(currentLevel, currentXP, requiredXP)
        Spacer(modifier = Modifier.height(18.dp))
        // ----------------- League code, copy & share button -----------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // League code
            Text(
                text = buildAnnotatedString {
                    append("Code de ligue : ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append(leagueID)
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Copy button
            IconButton(onClick = {
                clipboardManager.setText(AnnotatedString(leagueID))
                Toast.makeText(context, context.getString(R.string.copied_code), Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.ContentCopy, contentDescription = context.getString(R.string.copy))
            }
            // Share button
            IconButton(
                onClick = {
                    val shareMessage =
                        "Viens rejoindre la ligue $leagueName sur Thirsty Quest et partageons nos aventures de consommation ! \uD83C\uDF7B\n" +
                                "Voici mon code de ligue : $leagueID\n"
                    onShareClick(shareMessage)
                }
            ) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = context.getString(R.string.share))
            }
        }
    }
}
