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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getTotalDrinkVolume
import com.example.thirstyquest.db.getTotalMoneySpent
import com.example.thirstyquest.db.getPublicationCountByCategory
import com.example.thirstyquest.db.getAverageDrinkConsumption
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue


@Composable
fun UserStatsContent(authViewModel: AuthViewModel) { // TODO : calculer la boisson la plus consommée et afficher dans les préférences en fonction
    val currentUserUid by authViewModel.uid.observeAsState()
    var totalVolume by remember { mutableStateOf(0) }
    var totalMoneySpent by remember { mutableStateOf(0.0) }
    var totalBeerCount by remember { mutableStateOf(0) }
    var totalShotCount by remember { mutableStateOf(0) }
    var averageDayConsumption by remember { mutableStateOf(0.0) }
    var averageMonthConsumption by remember { mutableStateOf(0.0) }
    var averageYearConsumption by remember { mutableStateOf(0.0) }

    LaunchedEffect(currentUserUid) {
        currentUserUid?.let { uid ->
            totalVolume = getTotalDrinkVolume(uid)
            totalMoneySpent = getTotalMoneySpent(uid)
            totalBeerCount = getPublicationCountByCategory( "Biere blonde",uid)
            totalShotCount = getPublicationCountByCategory("shot",uid)
            averageDayConsumption = getAverageDrinkConsumption("DAY",uid)
            averageMonthConsumption = getAverageDrinkConsumption("MONTH",uid)
            averageYearConsumption = getAverageDrinkConsumption("YEAR",uid)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Consummation part
        Text(
            text = stringResource(R.string.conso),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItemColumn(stringResource(R.string.liters_per_day), "$averageDayConsumption")
            StatItemColumn(stringResource(R.string.liters_per_month), "$averageMonthConsumption")
            StatItemColumn(stringResource(R.string.liters_per_year), "$averageYearConsumption")
        }

        Spacer(modifier = Modifier.height(24.dp))
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Preferences part
        Text(
            text = stringResource(R.string.pref),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItemColumn("Biere blonde", "$totalBeerCount")
            StatItemColumn("Shot", "$totalShotCount")
        }
        Spacer(modifier = Modifier.height(24.dp))
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Total part
        Text(
            text = stringResource(R.string.total),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItemColumn(stringResource(R.string.consumed_drink), "$totalVolume litres ")
            StatItemColumn(stringResource(R.string.spent_money), "$totalMoneySpent €")

        }
    }
}



