package com.example.thirstyquest.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.thirstyquest.R

////////////////////////////////////////////////////////////////////////////////////////////////////
//     Composable

@Composable
fun StatItemColumn(label: String, value: String)
{
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun StatsCategory(category: String)
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
fun StatsItemRowValueFirst(label: String, value: String) {
    val displayValue = try {
        if (value != "-1" && label != "") {
            val number = value.toDouble()
            String.format("%.2f", number)
        } else value
    } catch (e: NumberFormatException) {
        value
    }

    if (displayValue != "-1" && label != "") {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayValue,
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
}


@Composable
fun StatsItemRowLabelFirst(label: String, value: String)
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
fun StatsDrink(totalLiters: Double, totalDrinks: Int)
{
    var selectedUnit by remember { mutableStateOf("Litres") }
    var expanded by remember { mutableStateOf(false) }

    // Mapping of unit & convert
    val unitConversions = mapOf(
        stringResource(R.string.liters) to 1.0,
        stringResource(R.string.drinks) to -1.0,
        stringResource(R.string.bath) to 150.0,
        stringResource(R.string.tank_truck) to 40000.0,
        stringResource(R.string.olymp_pool) to 2500000.0
    )

    val convertedValue =
        if(selectedUnit == stringResource(R.string.drinks)) {
            totalDrinks.toDouble()
        }
        else {
            totalLiters / (unitConversions[selectedUnit] ?: 1.0)
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
                    imageVector = if (expanded) Icons.AutoMirrored.Filled.ArrowBack else Icons.Filled.ArrowDownward,
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

@Composable
fun ConsumptionChart(
    pointsData : List<Point>,
    selectedDuration : String = "Semaines"
) {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Label selection
    val weekLabels = listOf("lu", "ma", "me", "je", "ve", "sa", "di")
    val monthLabels = (1..pointsData.size).map { it.toString() }
    val yearLabels = listOf("janvier", "fevrier", "mars", "avril", "mai", "juin", "juillet", "août", "sept.", "oct.", "nov.", "dec.")

    val xLabels = when (selectedDuration) {
        "Dans la semaine" -> weekLabels
        "Dans le mois" -> monthLabels
        "Dans l'année" -> yearLabels
        else -> List(pointsData.size) { it.toString() }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // X data
    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(MaterialTheme.colorScheme.background)
        .axisLabelColor(MaterialTheme.colorScheme.onBackground)
        .axisLineColor(MaterialTheme.colorScheme.onBackground)
        .steps(pointsData.size - 1)
        .labelData { i -> xLabels.getOrNull(i) ?: "" }
        .labelAndAxisLinePadding(20.dp)
        .startDrawPadding(0.dp)
        .build()

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Y data
    val yMax = pointsData.maxOfOrNull { it.y } ?: 100f
    val ySteps = if (yMax < 5) { yMax.toInt() } else { 5 }          // ySteps = 5 or yMax if yMax < 5
    val yAxisData = AxisData.Builder()
        .steps(ySteps)
        .backgroundColor(MaterialTheme.colorScheme.background)
        .axisLabelColor(MaterialTheme.colorScheme.onBackground)
        .axisLineColor(MaterialTheme.colorScheme.onBackground)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val yScale = yMax / ySteps
            String.format("%.1f", i * yScale)
        }.build()
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Line data
    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(color = MaterialTheme.colorScheme.secondary),
                    IntersectionPoint(color = MaterialTheme.colorScheme.primary),
                    SelectionHighlightPoint(color = MaterialTheme.colorScheme.tertiary),
                    ShadowUnderLine(color = MaterialTheme.colorScheme.secondary),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = MaterialTheme.colorScheme.onBackground),
        backgroundColor = MaterialTheme.colorScheme.background
    )

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Line chart
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData,
    )
}