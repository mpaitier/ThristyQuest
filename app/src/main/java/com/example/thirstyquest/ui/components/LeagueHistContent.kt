package com.example.thirstyquest.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBox
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thirstyquest.R

data class Publication(val ID: Int, val description: String, val user_ID: Int, val date: String, val heure: String, val category: String)

@Composable
fun LeagueHistScreenContent(leagueID: Int) {
    HistList(leagueID = leagueID)
}

//////////////////////////////////////////////////////////////////////////////////
//                                Composables

@Composable
fun HistList(leagueID: Int)
{
                                                                                                    // TODO : get all publications with leagueID, sorted by date (most recent first)
    val hist = listOf(
        Publication(1, "Pinte de bête rouge au Bistrot", 26, "12/02/2025", "19:00","Biere" ),
        Publication(2, "Aguardiente chez Moe's", 12, "12/02/2025", "20:00","Liqueur"),
        Publication(3, "Moscow Mule chez Croguy", 84, "12/02/2025", "20:12","Moscow Mule"),
        Publication(4, "Binch de malade", 2, "13/02/2025", "02:26","Biere"),
        Publication(5, "Ricard du midi", 1, "13/02/2025", "12:26","Ricard"),
        Publication(6, "Double IPA qui arrache", 4, "13/02/2025", "16:52","Biere"),
        Publication(7, "Bouteille de vin en mode classe", 18, "14/02/2025", "21:30", "Vin Rouge"),
        Publication(8, "Ricard pur x_x", 14, "15/02/2025", "19:15","Ricard"),
        Publication(9, "La potion de Shrek", 8, "15/02/2025", "01:26","Cimetière"),
        Publication(10, "Pinte à la Voie Maltée", 74, "16/02/2025", "10:28","Biere")
    )

    var isDescending by remember { mutableStateOf(true) }
    // sort by date then time
    var sortedHist = if (isDescending) {
        hist.sortedWith(compareBy({ it.date }, { it.heure })).reversed()
    } else {
        hist.sortedWith(compareBy({ it.date }, { it.heure }))
    }
    var publicationNum = 0

    Column {
        Row (verticalAlignment = Alignment.CenterVertically)
        {
            Text(
                text = stringResource(id = R.string.league_history),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1F))
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            Column {
                sortedHist.forEach { publication ->
                    histItem(publication, publicationNum)
                    publicationNum++
                }
            }

        }
    }
}

@Composable
fun histItem(publication: Publication, publicationNum: Int)
{
                                                                                                    // TODO : Add picture
                                                                                                    // TODO : Make picture clickable and navigate to publication details
                                                                                                    // TODO : Make user's name clickable and navigate to user's profile
    val name = "Membre n°${publication.user_ID}"                                                    // TODO : get member name with user_ID
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.AccountBox,
            contentDescription = "Publication picture",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column (modifier = Modifier.weight(1f)) {
            Text(
                publication.description,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                color = if (publicationNum % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column {
            Text(
                text = publication.date,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.End)
            )
            Text(
                text = publication.heure,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
