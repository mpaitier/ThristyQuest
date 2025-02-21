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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.R


data class Publication(val ID: Int, val description: String, val user_ID: Int, val date: String)


@Composable
fun MainMenuScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mini Menu "Top boissons"
            Text(stringResource(id = R.string.top_drinks), fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((0.23f* LocalConfiguration.current.screenHeightDp).dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                TopDrinkItem(Icons.Filled.WineBar, "Vin rouge", "3 000 points")
                TopDrinkItem(Icons.Filled.LocalDrink, "Bière rouge", "2 800 points")
                TopDrinkItem(Icons.Filled.LocalBar, "Gin Tonic", "2 600 points")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bouton Ajouter une consommation
            Button(
                onClick = { showDialog = true }, // Ouvrir le dialogue
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
                val hist = listOf(
                    Publication(1, "Pinte au Bistrot", 26, "19:00 12/02/2025"),
                    Publication(2, "Pinte chez Moe's", 12, "20:00 12/02/2025"),
                    Publication(3, "Moscow Mule chez Croguy", 84, "20:12 12/02/2025"),
                    Publication(4, "Binch de malade", 2, "02:26 13/02/2025"),
                    Publication(5, "Ricard du midi", 1, "12:26 13/02/2025"),
                    Publication(6, "Double IPA qui arrache", 4, "16:52 13/02/2025"),
                    Publication(7, "Bouteille de vin en mode classe", 18, "21:30 14/02/2025"),
                    Publication(8, "Ricard pur x_x", 14, "19:15 15/02/2025"),
                    Publication(9, "La potion de Shrek", 8, "01:26 15/02/2025"),
                    Publication(10, "Pinte à la Voie Maltée", 74, "10:28 16/02/2025")
                )

                val sortedHist = hist.sortedByDescending { it.date }

                sortedHist.forEach { publication ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        histItem(publication)
                    }
                }
            }
        }
    }

    // Affichage du dialogue
    if (showDialog) {
        AddDrinkDialog(onDismiss = { showDialog = false })
    }
}

@Composable
fun AddDrinkDialog(onDismiss: () -> Unit) {
    var drinkName by remember { mutableStateOf("") }
    var drinkPrice by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button( //TODO faire en sorte que lorsqu'on clique sur valider, la boisson soit ajoutée au catalogue
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(id = R.string.add), fontSize = 16.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // TODO remplacer le logo par l'image quand je pourrais l'avoir
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.LightGray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.LocalDrink, contentDescription = "Photo", tint = Color.DarkGray, modifier = Modifier.size(80.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TODO ajouter les autres options, à voir comment on fait pour la catégorie et le lieu
                OutlinedTextField(
                    value = drinkName,
                    onValueChange = { drinkName = it },
                    label = { Text("Nom de la boisson") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Champ pour le prix de la boisson
                OutlinedTextField(
                    value = drinkPrice,
                    onValueChange = { drinkPrice = it },
                    label = { Text("Prix de la boisson (€)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}


@Composable
fun TopDrinkItem(icon: androidx.compose.ui.graphics.vector.ImageVector, name: String, points: String) {
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


@Composable
fun histItem(publication: Publication) {
    // Création d'un item d'historique avec une description et une date
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.LocalDrink,  // Icone générique pour les boissons
            contentDescription = "Boisson",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                publication.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = publication.date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}