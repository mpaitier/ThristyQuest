package com.example.thirstyquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.example.thirstyquest.R
import com.example.thirstyquest.data.Publication
import com.example.thirstyquest.ui.dialog.AddPublicationDialog
import com.example.thirstyquest.ui.dialog.PublicationDetailDialog
import com.example.thirstyquest.ui.components.PublicationItemMenu
import com.example.thirstyquest.data.PublicationHist


@Composable
fun MainMenuScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var selectedPublication by remember { mutableStateOf<Publication?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show 3 best drinks
            Text(stringResource(id = R.string.top_drinks), fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TopDrinkItem(Icons.Filled.WineBar, "Vin rouge", "3 000 points")
                TopDrinkItem(Icons.Filled.LocalDrink, "Bière rouge", "2 800 points")
                TopDrinkItem(Icons.Filled.LocalBar, "Gin Tonic", "2 600 points")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bouton Ajouter une consommation
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(100.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = stringResource(id = R.string.add_drink))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.add_drink), fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Historique des boissons
            Text(stringResource(id = R.string.personal_hist), fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                val sortedHist = PublicationHist.sortedByDescending { it.date + it.hour }

                sortedHist.forEach { publication ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .clickable { selectedPublication = publication } // Details on click
                    ) {
                        PublicationItemMenu(publication)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddPublicationDialog(onDismiss = { showDialog = false })
    }

    // Show selected publication
    selectedPublication?.let {
        PublicationDetailDialog(publication = it, onDismiss = { selectedPublication = null })
    }
}


@Composable
fun TopDrinkItem(icon: androidx.compose.ui.graphics.vector.ImageVector, name: String, points: String)
{
    // TODO : function takes top drink items in data base, we shouldn't need to declare them manually
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = name,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = name, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(text = points, fontSize = 14.sp, color = Color.Gray)
    }
}
