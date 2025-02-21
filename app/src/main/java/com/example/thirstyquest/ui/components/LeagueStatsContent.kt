package com.example.thirstyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thirstyquest.R

@Composable
fun LeagueStatsScreenContent(leagueID: Int) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.league_stats),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        LeagueStatsList(leagueID = leagueID)
    }
}

//////////////////////////////////////////////////////////////////////////////////
//                                Composables
@Composable
fun LeagueStatsList(leagueID: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // =========================================================================================
        LeagueStatsCategory(leagueID, stringResource(R.string.league_members))
        val whoDrinkTheMost = "Alexandre"                                                           // TODO : get value with leagueID
        val whoDrinkTheMostLitersPerDay = 28                                                        // TODO : get value with leagueID
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            LeagueStatItem2("Le plus gros buveur", whoDrinkTheMost)
            Spacer(modifier = Modifier.weight(1f))
            LeagueStatItem("L/Jour", "$whoDrinkTheMostLitersPerDay")
        }
        val whoDrinkTheLess = "Vincent"                                                             // TODO : get value with leagueID
        val whoDrinkTheLessLitersPerDay = 0.006                                                     // TODO : get value with leagueID
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            LeagueStatItem2("Le plus sobre", whoDrinkTheLess)
            Spacer(modifier = Modifier.weight(1f))
            LeagueStatItem("L/Jour", "$whoDrinkTheLessLitersPerDay")
        }
        // - - - - - - - - - - - - - - - - - - - - - - - - - - -
        Spacer(modifier = Modifier.height(10.dp))
        // - - - - - - - - - - - - - - - - - - - - - - - - - - -
        val whoPaysTheMost = "Alexandre"                                                            // TODO : get value with leagueID
        val whoPaysTheMostTotal = 18900                                                             // TODO : get value with leagueID
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            LeagueStatItem2("Le plus généreux", whoPaysTheMost)
            Spacer(modifier = Modifier.weight(1f))
            LeagueStatItem("€ dépensés", "$whoPaysTheMostTotal")
        }
        val whoPaysTheLess = "Maxime"                                                               // TODO : get value with leagueID
        val whoPaysTheLessTotal = 2.30                                                              // TODO : get value with leagueID
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            LeagueStatItem2("Le plus gros rat", whoPaysTheLess)
            Spacer(modifier = Modifier.weight(1f))
            LeagueStatItem("€ dépensés", "$whoPaysTheLessTotal")
        }
        // =========================================================================================
        LeagueStatsCategory(leagueID, stringResource(R.string.league_conso))
        val litersPerDay = 5.2                                                                      // TODO : get value with leagueID
        Row( modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.liters),
                color = MaterialTheme.colorScheme.tertiary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LeagueStatItem("/Jour", "$litersPerDay")
                LeagueStatItem("/Mois", "${litersPerDay*30}")
                LeagueStatItem("/An", "${litersPerDay*365}")
            }
        }

        val drinksPerDay = 12.6                                                                     // TODO : get value with leagueID
        Row( modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.drinks),
                color = MaterialTheme.colorScheme.tertiary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                LeagueStatItem("/Jour", "$drinksPerDay")
                LeagueStatItem("/Mois", "${drinksPerDay * 30}")
                LeagueStatItem("/An", "${drinksPerDay * 365}")
            }
        }
        // =========================================================================================
        LeagueStatsCategory(leagueID, stringResource(R.string.league_pref))
        val firstDrink = "Cimetière"                                                                // TODO : get value with leagueID
        val secondDrink = "Ricard"                                                                  // TODO : get value with leagueID
        val thirdDrink = "Bière IPA"                                                                // TODO : get value with leagueID
        val firstDrinkValue = 159                                                                   // TODO : get value with leagueID
        val secondDrinkValue = 84                                                                   // TODO : get value with leagueID
        val thirdDrinkValue = 72                                                                    // TODO : get value with leagueID

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LeagueStatItem(firstDrink, "$firstDrinkValue")
            LeagueStatItem(thirdDrink, "$secondDrinkValue")
            LeagueStatItem(secondDrink, "$thirdDrinkValue")
        }

    }
}

@Composable
fun LeagueStatsCategory(leagueID: Int, category: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = category,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.primary,
        fontStyle = FontStyle.Italic
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun LeagueStatItem(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun LeagueStatItem2(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}