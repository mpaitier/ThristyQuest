package com.example.thirstyquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.example.thirstyquest.R

@Composable
fun MainMenuScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) } // État du dialogue

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
            Text(stringResource(id = R.string.top_boissons), fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
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
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Ajouter une consommation")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.ajouter_boisson), fontSize = 18.sp)
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
                Text("Ajouter", fontSize = 16.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
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
