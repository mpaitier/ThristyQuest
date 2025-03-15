package com.example.thirstyquest.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.thirstyquest.R

@Composable
fun StatisticsDialog(userID: Int, onDismiss: () -> Unit)
{                                                                                                   // TODO : set content
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.stats) + " de ami $userID" ) },
        text = {
            // Contenu
            Text("Contenu")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}