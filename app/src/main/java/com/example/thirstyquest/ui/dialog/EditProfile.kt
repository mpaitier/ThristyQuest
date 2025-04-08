package com.example.thirstyquest.ui.dialog

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.thirstyquest.db.getUserNameById
import com.example.thirstyquest.db.updateUserName
import com.example.thirstyquest.db.updateUserProfilePhotoUrl
import com.example.thirstyquest.db.uploadImageToFirebase
import com.example.thirstyquest.ui.viewmodel.AuthViewModel

@Composable
fun EditProfileDialog(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit,
    onPhotoUpdated: () -> Unit = {} // 👈 Ajouté ici
) {
    val currentUserUid by authViewModel.uid.observeAsState()
    var userName by remember { mutableStateOf("") }
    var newUserName by remember { mutableStateOf("") }

    var showImageDialog by remember { mutableStateOf(false) }
    var profileImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(currentUserUid) {
        currentUserUid?.let { uid ->
            getUserNameById(uid) { name ->
                userName = name ?: ""
                newUserName = name ?: ""
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Modifier le profil",
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

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { showImageDialog = true }) {
                    Text("Modifier la photo de profil")
                }

                profileImageBitmap?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                currentUserUid?.let { uid ->
                    updateUserName(uid, newUserName)

                    // 👇 Upload image si dispo
                    profileImageBitmap?.let { bitmap ->
                        uploadImageToFirebase(uid, bitmap) { url ->
                            url?.let {
                                updateUserProfilePhotoUrl(uid, it)
                                onPhotoUpdated() // 👈 Déclenche le rafraîchissement de la TopBar
                            }
                        }
                    }
                }
                onDismiss()
            }) {
                Text("Enregistrer", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = MaterialTheme.colorScheme.secondary)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )

    if (showImageDialog) {
        AddProfilePictureDialog(
            onDismiss = { showImageDialog = false },
            onImageCaptured = {
                profileImageBitmap = it
                showImageDialog = false
            }
        )
    }
}


@Composable
fun AddProfilePictureDialog(
    onDismiss: () -> Unit,
    onImageCaptured: (Bitmap) -> Unit
) {
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        if (it != null) capturedImage = it
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une photo de profil") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                capturedImage?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(onClick = { launcher.launch(null) }) {
                    Text("Prendre une photo")
                }
            }
        },
        confirmButton = {
            if (capturedImage != null) {
                TextButton(onClick = {
                    onImageCaptured(capturedImage!!)
                    onDismiss()
                }) {
                    Text("Valider")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ignorer")
            }
        }
    )
}

