package com.example.thirstyquest.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.data.PublicationHist


@Composable
fun LeagueHistScreenContent(leagueID: String, navController: NavController)
{
                                                                                                    // TODO : get all publications with leagueID, sorted by date (most recent first)
    var isDescending by remember { mutableStateOf(true) }
    // sort by date then time
    var sortedHist = if (isDescending) {
        PublicationHist.sortedWith(compareBy({ it.date }, { it.hour })).reversed()
    } else {
        PublicationHist.sortedWith(compareBy({ it.date }, { it.hour }))
    }
    var publicationNum = 0

    Column {
        // Title & sort button
        Row (verticalAlignment = Alignment.CenterVertically)
        {
            // Title
            Text(
                text = stringResource(id = R.string.league_history),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1F))
            // Sort button
            IconButton(
                onClick = { isDescending = !isDescending }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = "Clock",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(22.dp)
                    )
                    Icon(
                        imageVector = if (isDescending) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                        contentDescription = "Order by time",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of publication in the hist
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            Column {
                sortedHist.forEach { publication ->
                    PublicationItemLeague(publication, publicationNum, navController)
                    publicationNum++
                }
            }
        }
    }
}