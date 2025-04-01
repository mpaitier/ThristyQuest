package com.example.thirstyquest.ui.dialog

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thirstyquest.R
import com.example.thirstyquest.data.Publication
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun PublicationDetailDialog(publication: Publication, onDismiss: () -> Unit)
{

    val dateTimeString = "${publication.date} ${publication.hour}"
    val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
    val parsedDate = inputFormat.parse(dateTimeString) ?: ""

    val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.FRENCH)
    val formattedDate = outputFormat.format(parsedDate)


    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = publication.description,
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
                // Circle picture
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = publication.photo),
                        contentDescription = "Image de la boisson",
                        modifier = Modifier.size(110.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Points
                Text(
                    text = "Points: ${publication.points}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Formated date with hour
                Text(
                    text = "Date: $formattedDate",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Price
                Text(
                    text = "Prix: ${publication.price} €",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}

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
                    Image(
                        painter = painterResource(id = boisson.photo),
                        contentDescription = "Image de la boisson",
                        modifier = Modifier.size(110.dp)
                    )
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
fun AddPublicationDialog(userId: String, onDismiss: () -> Unit) {
    var drinkName by remember { mutableStateOf("") }
    var drinkPrice by remember { mutableStateOf("") }
    var drinkCategory by remember { mutableStateOf("") }
    var drinkVolume by remember { mutableStateOf(0) }
    val volumeOptions = listOf(4, 12, 25, 33, 50)
    var expanded by remember { mutableStateOf(false) }
    val db = FirebaseFirestore.getInstance()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                addPublicationToFirestore(userId, drinkName, drinkPrice, drinkCategory, drinkVolume)
                onDismiss()
            }) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = drinkName,
                    onValueChange = { drinkName = it },
                    label = { Text("Nom de la boisson") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = drinkPrice,
                    onValueChange = { drinkPrice = it },
                    label = { Text("Prix (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = drinkCategory,
                    onValueChange = { drinkCategory = it },
                    label = { Text("Catégorie") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
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
                                text = if (drinkVolume > 0) "$drinkVolume cl" else "Choisir un volume",
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
                        volumeOptions.forEach { volume ->
                            DropdownMenuItem(
                                text = { Text("$volume cl") },
                                onClick = {
                                    drinkVolume = volume
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

fun addPublicationToFirestore(userId: String, drinkName: String, drinkPrice: String, drinkCategory: String, drinkVolume: Int) {
    val db = FirebaseFirestore.getInstance()
    val id = UUID.randomUUID().toString() // Génère un ID unique
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    val currentTime = timeFormat.format(Date())
    val points = 500

    val publication = hashMapOf(
        "ID" to id,
        "description" to drinkName,
        "user_ID" to userId.toString(),  // Conversion de userId en String
        "date" to currentDate,
        "hour" to currentTime,
        "category" to drinkCategory,
        "volume" to drinkVolume,
        "price" to (drinkPrice.toDoubleOrNull() ?: 0.0),
        "photo" to "", // Placeholder pour la photo
        "points" to points
    )

    db.collection("publications")
        .document(id)
        .set(publication)
        .addOnSuccessListener { Log.d("Firebase", "Publication ajoutée avec succès") }
        .addOnFailureListener { e -> Log.w("Firebase", "Erreur lors de l'ajout", e) }
}