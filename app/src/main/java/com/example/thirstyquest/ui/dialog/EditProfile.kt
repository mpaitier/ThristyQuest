package com.example.thirstyquest.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EditProfileDialog(
    firstName: String,
    lastName: String,
    age: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var newFirstName by remember { mutableStateOf(firstName) }
    var newLastName by remember { mutableStateOf(lastName) }
    var newAge by remember { mutableStateOf(age) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                "Modifier le profil",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer // Text color
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextButton(onClick = {}) {
                    Text("Changer la photo de profil", color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newFirstName,
                    onValueChange = { newFirstName = it },
                    label = { Text("Prénom", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newLastName,
                    onValueChange = { newLastName = it },
                    label = { Text("Nom", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newAge,
                    onValueChange = { newAge = it },
                    label = { Text("Âge", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(newFirstName, newLastName, newAge)
                onDismiss()
            }) {
                Text("Enregistrer", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Annuler", color = MaterialTheme.colorScheme.primary)
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer // Background color
    )
}