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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.thirstyquest.R

data class Publication(val ID: Int, val description: String, val user_ID: Int, val date: String)

@Composable
fun LeagueHistScreenContent(leagueID: Int) {
    HistList()
}

//////////////////////////////////////////////////////////////////////////////////
//                                Composables
// TODO : Box pour stocker les éléments de l'historique
// TODO : Element historique: icone + nom + (date / qui)

@Composable
fun HistList() {
    // TODO : get all publications with leagueID, sorted by date (most recent first)
    val hist = listOf(
        Publication(1, "Pinte au Bistrot", 26, "19:00 12/02/2025"),
        Publication(2, "Pinte chez Moe's", 12, "20:00 12/02/2025"),
        Publication(3, "Moscow Mule chez Croguy", 84, "20:12 12/02/2025"),
        Publication(4, "Binch de malade", 2, "02:26 13/02/2025"),
        Publication(5, "Ricard du midi", 1, "12:26 13/02/2025"),
        Publication(6, "Double IPA qui arrache", 4, "16:52 13/02/2025"),
        Publication(7, "Bouteille de vin en mode classe", 18, "21:30 14/02/2025"),
        Publication(8, "Ricard pur x_x", 14, "19:15 15/02/2025"),
        Publication(9, "La potion de Shrek", 8, "01:26 15/02/2025"),
        Publication(10, "Pinte à la Voie Maltée", 74, "10:28 16/02/2025")
    )

    val sortedHist = hist.sortedByDescending { it.date }
    var publicationNum = 0

    Column {
        Text(
            text = stringResource(id = R.string.league_history),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
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
fun histItem(publication: Publication, publicationNum: Int) {
    // TODO : Add picture
    // TODO : Make picture clickable and navigate to publication details
    // TODO : Make user's name clickable and navigate to user's profile
    val name = "Membre n°${publication.user_ID}"        // TODO : get member name with user_ID
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

        Column {
            Text(
                publication.description,
                style = MaterialTheme.typography.bodyLarge,     // TODO : Make text bigger
                color = if (publicationNum % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }


        Spacer(modifier = Modifier.weight(1f))

        Column {
            // publication.date is "HH:MM DD/MM/YYYY", get two strings from it
            val date = publication.date.split(" ")
            Text(
                text = date[0],
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.End)
            )
            Text(
                text = date[1],
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}