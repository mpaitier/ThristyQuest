package com.example.thirstyquest.ui.dialog

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.thirstyquest.R
import com.example.thirstyquest.data.DrinkCategories
import com.example.thirstyquest.data.Publication
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.thirstyquest.db.addPublicationToFirestore
import com.example.thirstyquest.db.addPublicationToLeague
import com.example.thirstyquest.db.getAllUserLeague
import com.example.thirstyquest.db.uploadImageToFirebase
import kotlinx.coroutines.launch

@Composable
fun PublicationDetailDialog(publication: Publication, onDismiss: () -> Unit)
{

    val dateTimeString = "${publication.date} ${publication.hour}"
    val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
    val parsedDate = inputFormat.parse(dateTimeString) ?: ""

    val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.FRENCH)
    val formattedDate = outputFormat.format(parsedDate)
    var showImageFullscreen by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = publication.description,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { showImageFullscreen = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (publication.photo.startsWith("http")) {
                        AsyncImage(
                            model = publication.photo,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ricard),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Points: ${publication.points}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Date: $formattedDate", fontSize = 16.sp)
                Text("Prix: ${publication.price} €", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.close), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showImageFullscreen) {
        Dialog(onDismissRequest = { showImageFullscreen = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Image centrée
                if (publication.photo.startsWith("http")) {
                    AsyncImage(
                        model = publication.photo,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                            .align(Alignment.Center)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ricard),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                            .align(Alignment.Center)
                    )
                }

                // Bouton croix en haut à droite
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fermer",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(30.dp)
                        .clickable { showImageFullscreen = false }
                )
            }
        }
    }
}

@Composable
fun AddPublicationDialog(
    userId: String,
    onDismiss: () -> Unit,
    imageBitmap: Bitmap?,
    onSuccess: () -> Unit
) {
    var drinkName by remember { mutableStateOf("") }
    var drinkPrice by remember { mutableStateOf("") }
    var drinkCategory by remember { mutableStateOf("") }
    var drinkVolume by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val volumeOptions = listOf(
        "Shot (4cl)" to 4,
        "Demi/Verre (25cl)" to 25,
        "Bouteille (33cl)" to 33,
        "Pinte (50cl)" to 50,
        "Pichet (1L)" to 100
    )

    var expanded by remember { mutableStateOf(false) }
    var showNameError by remember { mutableStateOf(false) }
    var showCategoryError by remember { mutableStateOf(false) }
    var showVolumeError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    showNameError = drinkName.isBlank()
                    showCategoryError = drinkCategory.isBlank()
                    showVolumeError = (drinkVolume == 0)

                    if (showNameError || showCategoryError || showVolumeError) return@Button

                    coroutineScope.launch {
                        isLoading = true

                        val url = imageBitmap?.let { uploadImageToFirebase(userId, it) } ?: ""

                        val publicationInfo = addPublicationToFirestore(
                            userId, drinkName, drinkPrice, drinkCategory, drinkVolume, url
                        )

                        getAllUserLeague(uid = userId) { leagueList ->
                            leagueList.forEach { leagueId ->
                                addPublicationToLeague(
                                    pid = publicationInfo.first,
                                    lid = leagueId,
                                    price = drinkPrice.toDoubleOrNull() ?: 0.0,
                                    volume = drinkVolume.toDouble(),
                                    points = publicationInfo.second,
                                    category = drinkCategory
                                )
                            }
                        }

                        isLoading = false
                        onDismiss()
                        onSuccess()
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Ajouter")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Image capturée",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = drinkName,
                    onValueChange = {
                        drinkName = it
                        showNameError = false
                    },
                    label = { Text("Nom de la boisson") },
                    isError = showNameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (showNameError) {
                    Text("Le nom est requis", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = drinkPrice,
                    onValueChange = { drinkPrice = it },
                    label = { Text("Prix (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                CategoryDropdown(
                    selectedCategory = drinkCategory,
                    onCategorySelected = {
                        drinkCategory = it
                        showCategoryError = false
                    }
                )
                if (showCategoryError) {
                    Text("La catégorie est requise", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(30.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = volumeOptions.find { it.second == drinkVolume }?.first
                                    ?: "Choisir un volume",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Ouvrir le menu",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        volumeOptions.forEach { (label, volume) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    drinkVolume = volume
                                    showVolumeError = false
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (showVolumeError) {
                    Text("Le volume est requis", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        }
    )
}




/*
@Composable
fun addInfoLeagues(userID: String){
    val db = FirebaseFirestore.getInstance()
    try {
        val result = db.collection("users").document(userID).collection("leagues")

    }
}
*/

@Composable
fun PublicationListDialog(boisson: Publication, onDismiss: () -> Unit)
{

    val dateTimeString = "${boisson.date} ${boisson.hour}"
    val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
    val parsedDate = inputFormat.parse(dateTimeString) ?: ""

    val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.FRENCH)
    val formattedDate = outputFormat.format(parsedDate)


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
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (boisson.photo.startsWith("http")) {
                            AsyncImage(
                                model = boisson.photo,
                                contentDescription = "Image de la boisson",
                                modifier = Modifier.size(110.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ricard),
                                contentDescription = "Image par défaut",
                                modifier = Modifier.size(110.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Points
                Text(
                    text = "Points: ${boisson.points}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date formatée avec l'heure
                Text(
                    text = "Date: $formattedDate", // Affichage de la date et de l'heure formatées
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Prix
                Text(
                    text = "Prix: ${boisson.price} €",
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

@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val allCategories = DrinkCategories.basePoints.keys.toList()
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        // Bouton stylisé
        Card(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedCategory.ifEmpty { "Choisir une catégorie" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Ouvrir menu",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Menu déroulant
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            allCategories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}


