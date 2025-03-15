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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thirstyquest.R

@Composable
fun UserStatsContent() {
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
            StatItemColumn(stringResource(R.string.liters_per_day), "0.4")
            StatItemColumn(stringResource(R.string.liters_per_month), "15")
            StatItemColumn(stringResource(R.string.liters_per_year), "200")
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
            StatItemColumn("Biere blonde", "71")
            StatItemColumn("Fire ball", "32")
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
            StatItemColumn(stringResource(R.string.consumed_drink), "310")
        }
    }
}

