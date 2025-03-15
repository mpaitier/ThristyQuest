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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
//                                Composable
@Composable
fun LeagueStatsList(leagueID: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp, 8.dp, 16.dp, 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // =========================================================================================
        StatsCategory(stringResource(R.string.league_members))
        Spacer(modifier = Modifier.height(12.dp))
        val whoDrinkTheMost = "Alexandre"                                                           // TODO : get value with leagueID
        val whoDrinkTheMostLitersPerDay = 28                                                        // TODO : get value with leagueID
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            StatsItemRowLabelFirst(stringResource(R.string.whoDrinkTheMost), whoDrinkTheMost)
            Spacer(modifier = Modifier.weight(1f))
            StatsItemRowValueFirst(stringResource(R.string.liters_per_day), "$whoDrinkTheMostLitersPerDay")
        }
        val whoDrinkTheLess = "Vincent"                                                             // TODO : get value with leagueID
        val whoDrinkTheLessLitersPerDay = 0.006                                                     // TODO : get value with leagueID
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            StatsItemRowLabelFirst(stringResource(R.string.whoDrinkTheLess), whoDrinkTheLess)
            Spacer(modifier = Modifier.weight(1f))
            StatsItemRowValueFirst(stringResource(R.string.liters_per_day), "$whoDrinkTheLessLitersPerDay")
        }
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        Spacer(modifier = Modifier.height(10.dp))
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        val whoPaysTheMost = "Alexandre"                                                            // TODO : get value with leagueID
        val whoPaysTheMostTotal = 18900                                                             // TODO : get value with leagueID
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            StatsItemRowLabelFirst(stringResource(R.string.whoPaysTheMost), whoPaysTheMost)
            Spacer(modifier = Modifier.weight(1f))
            StatsItemRowValueFirst(stringResource(R.string.spent_money), "$whoPaysTheMostTotal")
        }
        val whoPaysTheLess = "Maxime"                                                               // TODO : get value with leagueID
        val whoPaysTheLessTotal = 2.30                                                              // TODO : get value with leagueID
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            StatsItemRowLabelFirst(stringResource(R.string.whoPaysTheLess), whoPaysTheLess)
            Spacer(modifier = Modifier.weight(1f))
            StatsItemRowValueFirst(stringResource(R.string.spent_money), "$whoPaysTheLessTotal")
        }
        // =========================================================================================
        Spacer(modifier = Modifier.height(12.dp))
        StatsCategory(stringResource(R.string.league_conso))
        Spacer(modifier = Modifier.height(12.dp))
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
                StatsItemRowValueFirst(stringResource(R.string.per_day), "$litersPerDay")
                StatsItemRowValueFirst(stringResource(R.string.per_month), "${litersPerDay*(365.25/12)}")
                StatsItemRowValueFirst(stringResource(R.string.per_year), "${litersPerDay*365.25}")
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

                StatsItemRowValueFirst("/Jour", "$drinksPerDay")
                StatsItemRowValueFirst("/Mois", "${drinksPerDay * 30}")
                StatsItemRowValueFirst("/An", "${drinksPerDay * 365}")
            }
        }
        // =========================================================================================
        Spacer(modifier = Modifier.height(12.dp))
        StatsCategory(stringResource(R.string.league_pref))
        Spacer(modifier = Modifier.height(12.dp))
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
            StatsItemRowValueFirst(firstDrink, "$firstDrinkValue")
            StatsItemRowValueFirst(thirdDrink, "$secondDrinkValue")
            StatsItemRowValueFirst(secondDrink, "$thirdDrinkValue")
        }

        // =========================================================================================
        Spacer(modifier = Modifier.height(12.dp))
        StatsCategory(stringResource(R.string.total))
        val totalPaid = 16000

        val totalDrink = 1457.toDouble()
        val totalLiters = 682.toDouble()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatsItemRowValueFirst(stringResource(R.string.spent_money), "$totalPaid")
            StatsDrink(totalLiters, totalDrink)
        }
    }
}
