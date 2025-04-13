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
import com.example.thirstyquest.data.Category
import com.example.thirstyquest.db.calculateLevelAndRequiredXP
import com.example.thirstyquest.db.getTop2CategoriesByTotal
import com.example.thirstyquest.db.getUserXPById


@Composable
fun UserStatsContent(authViewModel: AuthViewModel, userId: String, isFriend: Boolean) {

    var totalVolume by remember { mutableStateOf(0.0) }
    var totalMoneySpent by remember { mutableStateOf(0.0) }
    var totaldrink1 by remember { mutableStateOf<Category?>(null) }
    var totaldrink2 by remember { mutableStateOf<Category?>(null) }
    var averageDayConsumption by remember { mutableStateOf(0.0) }
    var averageMonthConsumption by remember { mutableStateOf(0.0) }
    var averageYearConsumption by remember { mutableStateOf(0.0) }

    var userXP by remember { mutableStateOf(0.0) }
    var userLevel by remember { mutableStateOf(1) }
    var requiredXP by remember { mutableStateOf(2000) }


    LaunchedEffect(userId) {
        totalVolume = getTotalDrinkVolume(userId)
        totalMoneySpent = getTotalMoneySpent(userId)
        val topCategories = getTop2CategoriesByTotal(userId)
        totaldrink1 = topCategories.getOrNull(0)
        totaldrink2 = topCategories.getOrNull(1)
        averageDayConsumption = getAverageDrinkConsumption("DAY",userId)
        averageMonthConsumption = getAverageDrinkConsumption("MONTH",userId)
        averageYearConsumption = getAverageDrinkConsumption("YEAR",userId)

        getUserXPById(userId) { xp ->
            userXP = xp ?: 0.0
            val (lvl, reqXP) = calculateLevelAndRequiredXP(xp?: 0.0)
            userLevel = lvl
            requiredXP = reqXP
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
            StatItemColumn(stringResource(R.string.cons_per_day), String.format("%.2f", averageDayConsumption))
            StatItemColumn(stringResource(R.string.cons_per_month), String.format("%.2f", averageMonthConsumption))
            StatItemColumn(stringResource(R.string.cons_per_year), String.format("%.2f", averageYearConsumption))
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
            totaldrink1?.let {
                StatItemColumn(it.name, it.total.toString())
            }

            totaldrink2?.let {
                StatItemColumn(it.name, it.total.toString())
            }

        }
        Spacer(modifier = Modifier.height(24.dp))

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // XP part

        if(!isFriend){
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Niveau du Profil",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProgressBar(
                currentLevel = userLevel,
                currentXP = (userXP % requiredXP).toInt(),
                requiredXP = requiredXP,
                modifier = Modifier.fillMaxWidth()
            )
        }

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
            StatItemColumn(stringResource(R.string.consumed_drink), String.format("%.2f", totalVolume))
            StatItemColumn(stringResource(R.string.spent_money), String.format("%.2f", totalMoneySpent))

        }
    }
}



