package com.example.thirstyquest.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.thirstyquest.data.Publication
import com.example.thirstyquest.db.getLeaguePublications


@Composable
fun LeagueHistScreenContent(leagueID: String, navController: NavController)
{
    var publicationList by remember { mutableStateOf<List<Publication>>(emptyList()) }
    var isDescending by remember { mutableStateOf(true) }

    var publicationsToShowCount by remember { mutableIntStateOf(10) } // Limit users to show
    LaunchedEffect(Unit)
    {
        publicationsToShowCount = 10
        publicationList = getLeaguePublications(leagueID = leagueID)
    }
    // sort by date then time
    var sortedHist = if (isDescending) {
        publicationList.sortedWith(compareBy({ it.date }, { it.hour })).reversed()
    } else {
        publicationList.sortedWith(compareBy({ it.date }, { it.hour }))
    }
    var publicationNum = 0

    Column (modifier = Modifier.fillMaxSize()) {
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

        if(publicationList == emptyList<Publication>())  {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        else {
            // List of publication in the hist
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    val histToDisplay = sortedHist.take(publicationsToShowCount)
                    histToDisplay.forEach { publication ->
                        PublicationItemLeague(publication, publicationNum, navController)
                        publicationNum++
                    }

                    if (publicationsToShowCount < sortedHist.size) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { publicationsToShowCount *= 2 },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.tertiary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                        ) {
                            Text(
                                stringResource(R.string.show_more),
                                color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}