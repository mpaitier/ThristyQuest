package com.example.thirstyquest.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Paramètres",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SettingsOption(
            title = "Mode sombre",
            description = "Activer ou désactiver le mode sombre de l'application",
            onClick = { /* Action pour activer/désactiver le mode sombre */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsOption(
            title = "Notifications",
            description = "Gérer les notifications de l'application",
            onClick = { /* Action pour gérer les notifications */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsOption(
            title = "Langue",
            description = "Changer la langue de l'application",
            onClick = { /* Action pour changer la langue */ }
        )
    }
}

@Composable
fun SettingsOption(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 16.sp
            )
        }
    }
}
