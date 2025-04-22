package com.example.thirstyquest.ui.dialog

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.thirstyquest.db.doesUsernameExist
import com.example.thirstyquest.db.getUserNameById
import com.example.thirstyquest.db.updateUserName
import com.example.thirstyquest.db.updateUserProfilePhotoUrl
import com.example.thirstyquest.db.uploadImageToFirebase
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditProfileDialog(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    val currentUserUid by authViewModel.uid.observeAsState()
    var userName by remember { mutableStateOf("") }
    var newUserName by remember { mutableStateOf("") }
    var showImageDialog by remember { mutableStateOf(false) }
    var profileImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var photoUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUserUid) {
        currentUserUid?.let { uid ->
            getUserNameById(uid) { name ->
                userName = name ?: ""
                newUserName = name ?: ""
            }

            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .await()
            photoUrl = snapshot.getString("photoUrl")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Modifier le profil", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = newUserName,
                    onValueChange = { newUserName = it },
                    label = { Text("Nom", color = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Affichage du message d'erreur si le pseudo est déjà pris
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (profileImageBitmap != null) {
                    Image(
                        bitmap = profileImageBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                } else if (!photoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Photo de profil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { showImageDialog = true }) {
                    Text("Modifier la photo de profil")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    // Vérifier si le pseudo existe déjà
                    if (doesUsernameExist(newUserName) && newUserName != userName) {
                        errorMessage = "Ce pseudo est déjà utilisé"
                        return@launch
                    }

                    currentUserUid?.let { uid ->
                        // Mettre à jour le pseudo si le pseudo est unique
                        updateUserName(uid, newUserName)
                        profileImageUri?.let { uri ->
                            val url = uploadImageToFirebase(uid, uri, context)
                            if (url != null) {
                                updateUserProfilePhotoUrl(uid, url)
                                authViewModel.refreshPhotoUrl()
                            }
                        }
                    }
                    onDismiss()
                }
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
            onImageCaptured = { uri ->
                profileImageUri = uri
                profileImageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                showImageDialog = false
            }
        )
    }
}


fun createImageFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfilePictureDialog(
    onDismiss: () -> Unit,
    onImageCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = photoUri
        if (success && uri != null) {
            onImageCaptured(uri)
            onDismiss()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Ajouter une photo de profil")
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val file = createImageFile(context)
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )
                    photoUri = uri
                    launcher.launch(uri)
                }) {
                    Text("Prendre une photo")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onDismiss) {
                    Text("Annuler")
                }
            }
        }
    }
}
