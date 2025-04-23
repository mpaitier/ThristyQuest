package com.example.thirstyquest.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ButtonDefaults
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
    var publicationList by remember { mutableStateOf<List<Publication>>( listOf( Publication(ID = "-1", description = "", user_ID = "", date = "", hour = "", category = "", price = 0.0, photo = "", points = -1 )) ) }
    var isDescending by remember { mutableStateOf(true) }

    var publicationsToShowCount by remember { mutableIntStateOf(10) } // Limit users to show, doesn't affect the loading of the data
    LaunchedEffect(Unit)
    {
        publicationsToShowCount = 10
        publicationList = getLeaguePublications(leagueID = leagueID)
    }
    // sort by date then hour
    var sortedHist = if (isDescending) {
        publicationList.sortedWith(compareBy({ it.date }, { it.hour })).reversed()
    } else {
        publicationList.sortedWith(compareBy({ it.date }, { it.hour }))
    }
    var publicationNum = 0

    Column (modifier = Modifier
        .fillMaxSize()
    ) {
        // ======================= Title & sort button =======================
        Row (verticalAlignment = Alignment.CenterVertically)
        {
            // -------- Title--------
            Text(
                text = stringResource(id = R.string.league_history),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1F))
            // -------- Sort button --------
            IconButton(
                onClick = { isDescending = !isDescending}
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
        // ======================= Content =======================
        if ( publicationList == listOf( Publication(ID = "-1", description = "", user_ID = "", date = "", hour = "", category = "", price = 0.0, photo = "", points = -1 )) )
        {
            LoadingSection()
        }
        else
        {
            // -------- List of publication in the hist --------
            Column (modifier = Modifier.verticalScroll(rememberScrollState())){
                if(publicationList.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_publication_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                            .verticalScroll(rememberScrollState())
                    )
                }
                else {
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
