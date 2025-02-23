package com.example.thirstyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thirstyquest.R
import com.example.thirstyquest.ui.screens.StatItem

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
            .verticalScroll(rememberScrollState())
    ) {
        // =========================================================================================
        LeagueStatsCategory(stringResource(R.string.league_members))
        Spacer(modifier = Modifier.height(12.dp))
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
        Spacer(modifier = Modifier.height(12.dp))
        LeagueStatsCategory(stringResource(R.string.league_conso))
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
        Spacer(modifier = Modifier.height(12.dp))
        LeagueStatsCategory(stringResource(R.string.league_pref))
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
            LeagueStatItem(firstDrink, "$firstDrinkValue")
            LeagueStatItem(thirdDrink, "$secondDrinkValue")
            LeagueStatItem(secondDrink, "$thirdDrinkValue")
        }

        // =========================================================================================
        Spacer(modifier = Modifier.height(12.dp))
        LeagueStatsCategory(stringResource(R.string.total))
        val totalPaid = 16000

        val totalDrink = 1457.toDouble()
        val totalLiters = 682.toDouble()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LeagueStatItem("€ dépensés", "$totalPaid")
            DrinkStats(totalLiters, totalDrink)
        }
    }
}

@Composable
fun LeagueStatsCategory(category: String)
{
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = category,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.primary,
        fontStyle = FontStyle.Italic
    )
}

@Composable
fun LeagueStatItem(label: String, value: String)
{
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
fun LeagueStatItem2(label: String, value: String)
{
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun DrinkStats(totalLiters: Double, totalDrinks: Double)
{
    var selectedUnit by remember { mutableStateOf("Litres") }
    var expanded by remember { mutableStateOf(false) }

    // Liste des unités et conversion
    val unitConversions = mapOf(
        stringResource(R.string.liters) to 1.0,
        stringResource(R.string.drinks) to 0.0,
        stringResource(R.string.bath) to 150.0,
        stringResource(R.string.tank_truck) to 40000.0,
        stringResource(R.string.olymp_pool) to 2500000.0
    )
    var convertedValue = 0.0
    if(unitConversions[selectedUnit] == 0.0){
        convertedValue = totalDrinks
    }
    else {
        convertedValue = totalLiters / (unitConversions[selectedUnit] ?: 1.0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Value
        Text(
            text = String.format("%.2f", convertedValue),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        // Unit selection
        Box {
            TextButton(onClick = { expanded = true }) {
                Text(selectedUnit, color = MaterialTheme.colorScheme.tertiary)
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowBack else Icons.Filled.ArrowDownward,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                unitConversions.keys.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            selectedUnit = unit
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
