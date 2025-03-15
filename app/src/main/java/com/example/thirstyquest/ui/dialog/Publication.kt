package com.example.thirstyquest.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thirstyquest.R
import com.example.thirstyquest.data.Publication
import java.text.SimpleDateFormat
import java.util.Locale

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
fun AddPublicationDialog(onDismiss: () -> Unit)
{
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