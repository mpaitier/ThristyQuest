package com.example.thirstyquest.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.thirstyquest.R
import com.example.thirstyquest.ui.components.AddPicture

@Composable
fun LeagueEditDialog(
    onDismiss: () -> Unit,
    onValidate: (String) -> Unit,
    leagueID: Int
) {
    var leagueName by remember { mutableStateOf("Ligue $leagueID") }          // TODO : get league name with leagueID
    // TODO : get league picture with leagueID

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(140.dp)
                ) {
                    // Add picture button
                    IconButton(
                        onClick = { /* TODO: Add picture selection */ },
                        modifier = Modifier
                            .size(48.dp)
                            .offset(x = 8.dp, y = 8.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircleOutline,
                            contentDescription = "Add picture",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // League's name entry
                OutlinedTextField(
                    value = leagueName,
                    onValueChange = { newLeagueName -> leagueName = newLeagueName },
                    label = { Text(stringResource(R.string.league_add_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Cancel & validate buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel button
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    // Validate button
                    Button(
                        onClick = { if (leagueName.isNotBlank()) onValidate(leagueName) },          // TODO : Add picture save
                        enabled = leagueName.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.validate))
                    }
                }
            }
        }
    }
}

@Composable
fun AddLeagueDialog(
    onDismiss: () -> Unit,
    onCreateLeague: () -> Unit,
    onJoinLeague: (String) -> Unit
)
{
    var leagueCode by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val maxCodeLength = 6

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Create a league
                Button(
                    onClick = onCreateLeague,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.league_create))
                }

                Divider(color = MaterialTheme.colorScheme.onBackground)

                // Enter league's code
                OutlinedTextField(
                    value = leagueCode,
                    onValueChange = {
                        if (it.length <= maxCodeLength) {
                            leagueCode = it
                            showError = false
                        }
                    },
                    label = { Text(text = stringResource(id = R.string.league_enter_code)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = stringResource(R.string.league_code_condition),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                // Join league button
                Button(
                    onClick = {
                        if (leagueCode.length == maxCodeLength) {
                            onJoinLeague(leagueCode)
                        } else {
                            showError = true
                        }
                    },
                    enabled = leagueCode.length == maxCodeLength,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.league_join))
                }
            }
        }
    )
}

// --------------------------------- Add League Dialog ---------------------------------

@Composable
fun CreateLeagueDialog(
    onDismiss: () -> Unit,
    onValidate: (String) -> Unit
) {
    var leagueName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddPicture()

                Spacer(modifier = Modifier.height(24.dp))

                // League's name entry
                OutlinedTextField(
                    value = leagueName,
                    onValueChange = { leagueName = it },
                    label = { Text(stringResource(R.string.league_add_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Cancel & validate buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel button
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    // Validate button
                    Button(
                        onClick = { if (leagueName.isNotBlank()) onValidate(leagueName) },  // TODO : Add picture save
                        enabled = leagueName.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.validate))
                    }
                }
            }
        }
    }
}
