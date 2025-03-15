package com.example.thirstyquest.ui.screens.profile


import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.example.thirstyquest.R


@Composable
fun SettingsScreen(navController: NavController) {

    // States for Switches
    var isDarkMode by remember { mutableStateOf(false) }
    var areNotificationsEnabled by remember { mutableStateOf(true) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mode sombre switch
        SettingsSwitchOption(
            title = stringResource(R.string.dark_mode),
            description = stringResource(R.string.dark_mode_detail),
            isChecked = isDarkMode,
            onCheckedChange = { isDarkMode = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications switch
        SettingsSwitchOption(
            title = stringResource(R.string.notif),
            description = stringResource(R.string.notif_detail),
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
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Déconnexion")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.disconnect))
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
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
            Text(text = stringResource(R.string.about), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.about_detail))
        }
    }
}