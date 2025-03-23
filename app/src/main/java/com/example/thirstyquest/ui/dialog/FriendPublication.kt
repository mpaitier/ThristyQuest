package com.example.thirstyquest.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.thirstyquest.R

@Composable
fun FriendPublicationDialog(userID: String, onDismiss: () -> Unit) {
    // TODO : show userID's hist
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Historique des publications") },
        text = {
            Text("Affichage de l'historique des publications de l'utilisateur.")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}