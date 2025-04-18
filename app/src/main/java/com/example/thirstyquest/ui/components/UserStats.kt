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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.thirstyquest.db.getAverageDrinkConsumption
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import co.yml.charts.common.model.Point
import com.example.thirstyquest.data.Category
import com.example.thirstyquest.db.calculateLevelAndRequiredXP
import com.example.thirstyquest.db.getMonthConsumptionPoints
import com.example.thirstyquest.db.getMonthVolumeConsumptionPoints
import com.example.thirstyquest.db.getTop2CategoriesByTotal
import com.example.thirstyquest.db.getUserXPById
import com.example.thirstyquest.db.getWeekConsumptionPoints
import com.example.thirstyquest.db.getWeekVolumeConsumptionPoints
import com.example.thirstyquest.db.getYearConsumptionPoints
import com.example.thirstyquest.db.getYearVolumeConsumptionPoints


@Composable
fun UserStatsContent(userId: String, isFriend: Boolean) {

    var weeklyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var monthlyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var yearlyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }

    var weeklyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var monthlyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var yearlyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var showedList by remember { mutableStateOf<List<Point>>(listOf(Point(0f, 0f))) }

    var totalVolume by remember { mutableDoubleStateOf(0.0) }
    var totalMoneySpent by remember { mutableDoubleStateOf(0.0) }

    var totalDrink1 by remember { mutableStateOf<Category?>(null) }
    var totalDrink2 by remember { mutableStateOf<Category?>(null) }
    var averageDayConsumption by remember { mutableDoubleStateOf(0.0) }
    var averageMonthConsumption by remember { mutableDoubleStateOf(0.0) }
    var averageYearConsumption by remember { mutableDoubleStateOf(0.0) }

    var userXP by remember { mutableDoubleStateOf(0.0) }
    var userLevel by remember { mutableIntStateOf(1) }
    var requiredXP by remember { mutableIntStateOf(2000) }


    LaunchedEffect(userId) {
        getUserXPById(userId) { xp ->
            userXP = xp ?: 0.0
            val (lvl, reqXP) = calculateLevelAndRequiredXP(xp?: 0.0)
            userLevel = lvl
            requiredXP = reqXP
        }

        totalVolume = getTotalDrinkVolume(userId)
        totalMoneySpent = getTotalMoneySpent(userId)

        val topCategories = getTop2CategoriesByTotal(userId)
        totalDrink1 = topCategories.getOrNull(0)
        totalDrink2 = topCategories.getOrNull(1)

        averageDayConsumption = getAverageDrinkConsumption("DAY",userId)
        averageMonthConsumption = getAverageDrinkConsumption("MONTH",userId)
        averageYearConsumption = getAverageDrinkConsumption("YEAR",userId)

        weeklyConsumptionList = getWeekConsumptionPoints(userId, "users")
        monthlyConsumptionList = getMonthConsumptionPoints(userId, "users")
        yearlyConsumptionList = getYearConsumptionPoints(userId, "users")

        weeklyVolumeList = getWeekVolumeConsumptionPoints(userId, "users")
        monthlyVolumeList = getMonthVolumeConsumptionPoints(userId, "users")
        yearlyVolumeList = getYearVolumeConsumptionPoints(userId, "users")
        showedList = weeklyConsumptionList
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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

        // old stats ?
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItemColumn(stringResource(R.string.cons_per_day), String.format("%.2f", averageDayConsumption))
            StatItemColumn(stringResource(R.string.cons_per_month), String.format("%.2f", averageMonthConsumption))
            StatItemColumn(stringResource(R.string.cons_per_year), String.format("%.2f", averageYearConsumption))
        }

        // Duration & volume selection
        var selectedDuration by remember { mutableStateOf("Dans la semaine") }
        var durationExpanded by remember { mutableStateOf(false) }
        val durationSelection = listOf("Dans la semaine", "Dans le mois", "Dans l'année")

        var selectedVolume by remember { mutableStateOf("Verres consommés") }
        var volumeExpanded by remember { mutableStateOf(false) }
        val volumeSelection = listOf("Verres consommés", "Litres consommés")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                TextButton(onClick = { volumeExpanded = true }) {
                    Text(selectedVolume, color = MaterialTheme.colorScheme.tertiary)
                    Icon(
                        imageVector = if (volumeExpanded) Icons.AutoMirrored.Filled.ArrowBack else Icons.Filled.ArrowDownward,
                        contentDescription = "Dropdown",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                DropdownMenu(
                    expanded = volumeExpanded,
                    onDismissRequest = { volumeExpanded = false }
                ) {
                    volumeSelection.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit) },
                            onClick = {
                                selectedVolume = unit
                                when (unit) {
                                    "Verres consommés" ->
                                        when (selectedDuration) {
                                            "Dans la semaine" -> showedList =
                                                weeklyConsumptionList

                                            "Dans le mois" -> showedList =
                                                monthlyConsumptionList

                                            "Dans l'année" -> showedList = yearlyConsumptionList
                                        }

                                    "Litres consommés" ->
                                        when (selectedDuration) {
                                            "Dans la semaine" -> showedList = weeklyVolumeList
                                            "Dans le mois" -> showedList = monthlyVolumeList
                                            "Dans l'année" -> showedList = yearlyVolumeList
                                        }
                                }
                                volumeExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1F))
            Box {
                TextButton(onClick = { durationExpanded = true }) {
                    Text(selectedDuration, color = MaterialTheme.colorScheme.tertiary)
                    Icon(
                        imageVector = if (durationExpanded) Icons.AutoMirrored.Filled.ArrowBack else Icons.Filled.ArrowDownward,
                        contentDescription = "Dropdown",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                DropdownMenu(
                    expanded = durationExpanded,
                    onDismissRequest = { durationExpanded = false }
                ) {
                    durationSelection.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit) },
                            onClick = {
                                selectedDuration = unit
                                when (unit) {
                                    "Dans la semaine" -> when (selectedVolume) {
                                        "Verres consommés" -> showedList = weeklyConsumptionList
                                        "Litres consommés" -> showedList = weeklyVolumeList
                                    }

                                    "Dans le mois" -> when (selectedVolume) {
                                        "Verres consommés" -> showedList =
                                            monthlyConsumptionList

                                        "Litres consommés" -> showedList = monthlyVolumeList
                                    }

                                    "Dans l'année" -> when (selectedVolume) {
                                        "Verres consommés" -> showedList = yearlyConsumptionList
                                        "Litres consommés" -> showedList = yearlyVolumeList
                                    }
                                }
                                durationExpanded = false
                            }
                        )
                    }
                }
            }
        }

        if (
            weeklyConsumptionList == listOf(Point(-1f, -1f)) ||
            monthlyConsumptionList == listOf(Point(-1f, -1f)) ||
            yearlyConsumptionList == listOf(Point(-1f, -1f)) ||
            weeklyVolumeList == listOf(Point(-1f, -1f)) ||
            monthlyVolumeList == listOf(Point(-1f, -1f)) ||
            yearlyVolumeList == listOf(Point(-1f, -1f))
        ) {
            LoadingSection()
        }
        else {
            ConsumptionChart(showedList, selectedDuration)
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
            totalDrink1?.let {
                StatItemColumn(it.name, it.total.toString())
            }

            totalDrink2?.let {
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
            StatItemColumn(stringResource(R.string.consumed_drink), String.format("%.2f", totalVolume))
            StatItemColumn("€ "+stringResource(R.string.spent_money), String.format("%.2f", totalMoneySpent))

        }
    }
}



