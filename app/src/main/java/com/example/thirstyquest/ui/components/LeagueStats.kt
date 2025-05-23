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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.Point
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getLeagueTotalLiters
import com.example.thirstyquest.db.getLeagueTotalPrice
import com.example.thirstyquest.db.getLeagueTotalPublications
import com.example.thirstyquest.db.getLeagueUserStats
import com.example.thirstyquest.db.getMonthConsumptionPoints
import com.example.thirstyquest.db.getMonthVolumeConsumptionPoints
import com.example.thirstyquest.db.getTop3Categories
import com.example.thirstyquest.db.getWeekConsumptionPoints
import com.example.thirstyquest.db.getWeekVolumeConsumptionPoints
import com.example.thirstyquest.db.getYearConsumptionPoints
import com.example.thirstyquest.db.getYearVolumeConsumptionPoints

@Composable
fun LeagueStatsScreenContent(leagueID: String) {
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

@Composable
fun LoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LeagueStatsList(leagueID: String) {

    // === Data ===
    var userStats by remember { mutableStateOf<List<Pair<String,Double>>>( listOf(Pair("", 0.0),Pair("", 0.0),Pair("", 0.0),Pair("", 0.0)) ) }
    // Consumption stats
    var weeklyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var monthlyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var yearlyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }

    var weeklyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var monthlyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var yearlyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var showedList by remember { mutableStateOf<List<Point>>(listOf(Point(0f, 0f))) }

    // Top categories
    var topCategories by remember { mutableStateOf<List<Pair<String,Long>>>( listOf(Pair("", -1L),Pair("", -1L),Pair("", -1L)) ) }

    // Total league stats
    var totalPaid by remember { mutableDoubleStateOf(-1.0) }
    var totalDrink by remember { mutableIntStateOf(-1) }
    var totalLiters by remember { mutableDoubleStateOf(-1.0) }

    LaunchedEffect(Unit) {
        userStats = getLeagueUserStats(leagueID)

        topCategories = getTop3Categories(leagueID)

        totalPaid = getLeagueTotalPrice(leagueID)
        totalDrink = getLeagueTotalPublications(leagueID)
        totalLiters = getLeagueTotalLiters(leagueID)

        weeklyConsumptionList = getWeekConsumptionPoints(leagueID, "leagues")
        monthlyConsumptionList = getMonthConsumptionPoints(leagueID, "leagues")
        yearlyConsumptionList = getYearConsumptionPoints(leagueID, "leagues")

        weeklyVolumeList = getWeekVolumeConsumptionPoints(leagueID, "leagues")
        monthlyVolumeList = getMonthVolumeConsumptionPoints(leagueID, "leagues")
        yearlyVolumeList = getYearVolumeConsumptionPoints(leagueID, "leagues")
        showedList = weeklyConsumptionList
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp, 8.dp, 16.dp, 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // =========================================================================================
        StatsCategory(stringResource(R.string.league_members))
        // ------------------------- Members stats -------------------------
        Spacer(modifier = Modifier.height(12.dp))
        if (
            userStats == listOf(Pair("", 0.0),Pair("", 0.0),Pair("", 0.0),Pair("", 0.0))
        ) {
            LoadingSection()
        }
        else {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                StatsItemRowLabelFirst(stringResource(R.string.whoDrinkTheMost), userStats[0].first)
                Spacer(modifier = Modifier.weight(1f))
                StatsItemRowValueFirst(
                    stringResource(R.string.liters),
                    "${userStats[0].second}"
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                StatsItemRowLabelFirst(stringResource(R.string.whoDrinkTheLess), userStats[1].first)
                Spacer(modifier = Modifier.weight(1f))
                StatsItemRowValueFirst(
                    stringResource(R.string.liters),
                    "${userStats[1].second}"
                )
            }
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            Spacer(modifier = Modifier.height(10.dp))
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                StatsItemRowLabelFirst(stringResource(R.string.whoPaysTheMost), userStats[2].first)
                Spacer(modifier = Modifier.weight(1f))
                StatsItemRowValueFirst(
                    (" € " + stringResource(R.string.spent_money)),
                    "${userStats[2].second}"
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                StatsItemRowLabelFirst(stringResource(R.string.whoPaysTheLess), userStats[3].first)
                Spacer(modifier = Modifier.weight(1f))
                StatsItemRowValueFirst(
                    (" € " + stringResource(R.string.spent_money)),
                    "${userStats[3].second}"
                )
            }
        }
        // =========================================================================================
        Spacer(modifier = Modifier.height(12.dp))
        StatsCategory(stringResource(R.string.league_conso))
        Spacer(modifier = Modifier.height(12.dp))
        // ------------------------- Duration & volume selection -------------------------
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
        // ------------------------- Graph -------------------------
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
        // =========================================================================================
        Spacer(modifier = Modifier.height(12.dp))
        StatsCategory(stringResource(R.string.league_pref))
        Spacer(modifier = Modifier.height(12.dp))
        // ------------------------- Preferences stats -------------------------
        if ( topCategories == listOf(Pair("", -1L),Pair("", -1L),Pair("", -1L)) ) {
            LoadingSection()
        }
        else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                StatsItemRowValueFirst(topCategories[0].first, "${topCategories[0].second}")
                StatsItemRowValueFirst(topCategories[1].first, "${topCategories[1].second}")
                StatsItemRowValueFirst(topCategories[2].first, "${topCategories[2].second}")
            }
        }
        // =========================================================================================
        Spacer(modifier = Modifier.height(12.dp))
        StatsCategory(stringResource(R.string.total))
        // ------------------------- Total stats -------------------------
        if (
            totalPaid == -1.0 ||
            totalDrink == -1 ||
            totalLiters == -1.0
        ) {
            LoadingSection()
        }
        else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatsItemRowValueFirst(
                    " € " + stringResource(R.string.spent_money),
                    "$totalPaid"
                ) // totalPaid
                StatsDrink(totalLiters, totalDrink)
            }
        }
    }
}
