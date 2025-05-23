package com.example.thirstyquest.ui.dialog


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getLeagueName
import com.example.thirstyquest.db.updateLeagueName
import com.example.thirstyquest.db.updateLeaguePhotoUrl
import com.example.thirstyquest.db.uploadLeagueImageToFirebase
import kotlinx.coroutines.launch

@Composable
fun LeagueEditDialog(
    onDismiss: () -> Unit,
    onValidate: (String) -> Unit,
    leagueID: String,
    leaguePhotoUrl: String
) {
    var leagueName by remember { mutableStateOf("") }
    var newLeagueName by remember { mutableStateOf("") }
    val context = LocalContext.current
    val newPhotoUri = remember { mutableStateOf<Uri?>(null) }
    val imageFile = remember { createImageFile(context) }
    val imageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            newPhotoUri.value = imageUri
        }
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(leagueID) {
        leagueName = getLeagueName(leagueID)
        newLeagueName = getLeagueName(leagueID)
    }

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
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(140.dp)) {
                    if (newPhotoUri.value != null) {
                        Image(
                            painter = rememberAsyncImagePainter(newPhotoUri.value),
                            contentDescription = "League image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                        )
                    }
                    else if (leaguePhotoUrl != "null") {
                        AsyncImage(
                            model = leaguePhotoUrl,
                            contentDescription = "League image",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else {
                        Image(
                            painter = painterResource(R.drawable.league_logo),
                            contentDescription = "League image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                        )
                    }

                    IconButton(
                        onClick = { launcher.launch(imageUri) },
                        modifier = Modifier
                            .size(48.dp)
                            .offset(x = 8.dp, y = 8.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircleOutline,
                            contentDescription = stringResource(R.string.take_picture),
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
                        onClick = {
                            scope.launch {
                                if (leagueName.isNotBlank()) {
                                    updateLeagueName(leagueID, leagueName)
                                    onValidate(leagueName)
                                }

                                if (newPhotoUri.value != null) {
                                    uploadLeagueImageToFirebase(leagueID, newPhotoUri.value!!, context) { url ->
                                        if (url != null) {
                                            updateLeaguePhotoUrl(leagueID, url)
                                        }
                                        onDismiss()
                                    }
                                } else {
                                    onDismiss()
                                }
                            }
                        },
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

                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground)

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

@Composable
fun CreateLeagueDialog(
    onDismiss: () -> Unit,
    onValidate: (String, Uri?) -> Unit
) {
    var leagueName by remember { mutableStateOf("") }
    val context = LocalContext.current
    val photoUri = remember { mutableStateOf<Uri?>(null) }
    val imageFile = remember { createImageFile(context) }
    val imageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            photoUri.value = imageUri
        }
    }

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
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(140.dp)) {
                    // --------------------- League picture ---------------------
                    if (photoUri.value != null) {
                        Image(
                            painter = rememberAsyncImagePainter(photoUri.value),
                            contentDescription = "League image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.league_logo),
                            contentDescription = "League image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                        )
                    }

                    IconButton(
                        onClick = { launcher.launch(imageUri) },
                        modifier = Modifier
                            .size(48.dp)
                            .offset(x = 8.dp, y = 8.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircleOutline,
                            contentDescription = stringResource(R.string.take_picture),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = leagueName,
                    onValueChange = { leagueName = it },
                    label = { Text(stringResource(R.string.league_add_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = { if (leagueName.isNotBlank()) onValidate(leagueName, photoUri.value) },
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
