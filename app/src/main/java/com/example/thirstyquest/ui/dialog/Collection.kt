package com.example.thirstyquest.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.thirstyquest.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess





@Composable
fun CollectionDialog(userID: String, onDismiss: () -> Unit) {
    val collectionItems = remember { mutableStateListOf<Pair<String, Int>>() }
    var expanded by remember { mutableStateOf(false) }

    // Charger les données dès que le composant est affiché
    LaunchedEffect(userID) {
        val db = FirebaseFirestore.getInstance()
        val result = db.collection("users")
            .document(userID)
            .collection("collection")
            .orderBy("points", Query.Direction.DESCENDING)
            .get()
            .await()

        collectionItems.clear()
        for (doc in result) {
            val name = doc.getString("name") ?: "Inconnu"
            val points = doc.getLong("points")?.toInt() ?: 0
            collectionItems.add(name to points)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.collec) + " de l'ami")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val itemsToDisplay = if (!expanded) collectionItems.take(3) else collectionItems

                itemsToDisplay.forEach { (name, points) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = name, modifier = Modifier.weight(1f))
                        Text(text = "$points pts", fontWeight = FontWeight.Bold)
                    }
                }

                // Bouton pour afficher plus
                if (!expanded && collectionItems.size > 3) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.ExpandMore, contentDescription = "Voir plus")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Voir tout")
                    }
                } else if (expanded && collectionItems.size > 3) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { expanded = false }) {
                        Icon(Icons.Filled.ExpandLess, contentDescription = "Réduire")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Réduire")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}
