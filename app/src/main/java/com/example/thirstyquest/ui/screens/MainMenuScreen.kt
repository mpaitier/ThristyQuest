package com.example.thirstyquest.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.thirstyquest.R


data class Publication(
    val ID: Int,
    val description: String,
    val user_ID: Int,
    val date: String,
    val prix: Double,
    val photo: Int,
    val nbrPoints: Int
)



@Composable
fun MainMenuScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedBoisson by remember { mutableStateOf<Publication?>(null) }

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
                val hist = listOf(
                    Publication(1, "Pinte au Bistrot", 26, "19:00 12/02/2025", 5.50, R.drawable.biere, 50),
                    Publication(2, "Pinte chez Moe's", 12, "20:00 12/02/2025", 6.00, R.drawable.biere, 60),
                    Publication(3, "Moscow Mule chez Croguy", 84, "20:12 12/02/2025", 8.50, R.drawable.vodka, 80),
                    Publication(4, "Binch de malade", 2, "02:26 13/02/2025", 4.00, R.drawable.biere, 40),
                    Publication(5, "Ricard du midi", 1, "12:26 13/02/2025", 3.00, R.drawable.ricard, 30)
                )


                val sortedHist = hist.sortedByDescending { it.date }

                sortedHist.forEach { publication ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .clickable { selectedBoisson = publication } // Ajout du clic pour afficher les détails
                    ) {
                        histItem(publication)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddDrinkDialog(onDismiss = { showDialog = false })
    }

    // Affichage des détails de la boisson sélectionnée
    selectedBoisson?.let {
        AffichageBoissonHisto(boisson = it, onDismiss = { selectedBoisson = null })
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = publication.photo),
            contentDescription = "Image de la boisson",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(publication.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            Text("Points: ${publication.nbrPoints}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        }
    }
}


@Composable
fun AffichageBoissonHisto(boisson: Publication, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = boisson.description,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Image circulaire
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = boisson.photo),
                        contentDescription = "Image de la boisson",
                        modifier = Modifier.size(110.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Points
                Text(
                    text = "Points: ${boisson.nbrPoints}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date
                Text(
                    text = "Date: ${boisson.date}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Prix
                Text(
                    text = "Prix: ${boisson.prix} €",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}



