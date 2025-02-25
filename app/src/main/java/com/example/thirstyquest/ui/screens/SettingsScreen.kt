package com.example.thirstyquest.ui.screens


import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment


@Composable
fun SettingsScreen(navController: NavController) {
    val primaryColor = MaterialTheme.colorScheme.primary

    // States for Switches
    var isDarkMode by remember { mutableStateOf(false) }
    var areNotificationsEnabled by remember { mutableStateOf(true) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Paramètres",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mode sombre switch
        SettingsSwitchOption(
            title = "Mode sombre",
            description = "Activer ou désactiver le mode sombre de l'application",
            isChecked = isDarkMode,
            onCheckedChange = { isDarkMode = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications switch
        SettingsSwitchOption(
            title = "Notifications",
            description = "Activer ou désactiver les notifications",
            isChecked = areNotificationsEnabled,
            onCheckedChange = { areNotificationsEnabled = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AboutSection()
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Button(
                onClick = {  }, // Fonction de déconnexion
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.Filled.Logout, contentDescription = "Déconnexion")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Déconnexion")
            }
        }
    }
}
@Composable
fun SettingsSwitchOption(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor
        )
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
            Spacer(modifier = Modifier.height(8.dp))

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
    }
}

@Composable
fun AboutSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "À propos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "ThirstyQuest est une application de suivi de consommation de boissons alcoolisées. Compare toi à tes amis et découvre tes stats !")
        }
    }
}