package com.example.thirstyquest.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.thirstyquest.db.getUserNameById
import com.example.thirstyquest.db.updateUserName
import com.example.thirstyquest.ui.viewmodel.AuthViewModel

@Composable
fun EditProfileDialog(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    val currentUserUid by authViewModel.uid.observeAsState()
    var userName by remember { mutableStateOf("") }
    var newUserName by remember { mutableStateOf("") }

    LaunchedEffect(currentUserUid) {
        currentUserUid?.let { uid ->
            getUserNameById(uid) { name ->
                userName = name ?: ""
                newUserName = name ?: ""
            }
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                "Modifier le nom",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = newUserName,
                    onValueChange = { newUserName = it },
                    label = { Text("Nom", color = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                currentUserUid?.let { uid ->
                    updateUserName(uid, newUserName)
                }
                onDismiss()
            }) {
                Text("Enregistrer", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Annuler", color = MaterialTheme.colorScheme.secondary)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
