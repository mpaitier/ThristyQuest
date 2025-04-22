package com.example.thirstyquest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.data.Publication
import com.example.thirstyquest.ui.dialog.AddPublicationDialog
import com.example.thirstyquest.ui.dialog.PublicationDetailDialog
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.thirstyquest.db.DrinkPointManager.getAllDrinksFromFirestore
import com.example.thirstyquest.db.DrinkPointManager.getTopDrinksFromFirestore
import com.example.thirstyquest.db.getUserLastPublications
import com.example.thirstyquest.ui.dialog.AllDrinksDialog
import com.example.thirstyquest.ui.dialog.TopDrinkItem
import com.example.thirstyquest.ui.dialog.WarningDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainMenuScreen(authViewModel: AuthViewModel, navController: NavController)
{
    val context = LocalContext.current
    val userId by authViewModel.uid.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var selectedPublication by remember { mutableStateOf<Publication?>(null) }
    var publications by remember { mutableStateOf<List<Publication>>(emptyList()) }
    var showAllDrinksDialog by remember { mutableStateOf(false) }
    var topDrinks by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var allDrinks by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    var showAlcoholWarningDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        if (!authViewModel.hasShownAlcoholWarning.value) {
            showAlcoholWarningDialog = true
        }
        // Charger les points des boissons
        topDrinks = getTopDrinksFromFirestore()
        allDrinks = getAllDrinksFromFirestore()
    }

    val photoUri = remember { mutableStateOf<Uri?>(null) }

    fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && photoUri.value != null) {
            showDialog = true
        }
    }

    // Charger les publications de l'utilisateur
    LaunchedEffect(userId) {
        userId?.let { uid ->
            getUserLastPublications(uid) { newList -> publications = newList }
            if (publications.size > 10) publications = publications.take(10)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAllDrinksDialog = true }
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.top_drinks),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Plus d'infos +",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                topDrinks.forEach { (name, points) ->
                    TopDrinkItem(name = name, points = "$points points")
                }
            }


            Spacer(modifier = Modifier.height(32.dp))

            // Bouton Ajouter une consommation
            Button(
                onClick = {
                    if (userId != null) {
                        val imageFile = createImageFile()
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            imageFile
                        )
                        photoUri.value = uri
                        takePictureLauncher.launch(uri)

                    }
                    else navController.navigate("login")
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(100.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.add_drink), fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Historique
            Text(stringResource(id = R.string.personal_hist), fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                publications.forEach { pub ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .clickable { selectedPublication = pub }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            ) {
                                if (pub.photo.startsWith("http")) {
                                    AsyncImage(
                                        model = pub.photo,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.ricard),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(pub.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                                Text("Points: ${pub.points}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Dialog to add a publication
    if (showDialog && userId != null) {
        AddPublicationDialog(
            userId = userId!!,
            imageUri = photoUri.value,
            onDismiss = {
                showDialog = false
            },
            onSuccess = {
                coroutineScope.launch {
                    Toast.makeText(context, "Publication ajoutée avec succès", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Dialog publication detail
    selectedPublication?.let {
         PublicationDetailDialog(publication = it, onDismiss = { selectedPublication = null })
    }

    // Dialog with all drinks
    if (showAllDrinksDialog) {
        AllDrinksDialog(
            drinks = allDrinks,
            onDismiss = { showAllDrinksDialog = false }
        )
    }

    // Preventive dialog
    if (showAlcoholWarningDialog && !authViewModel.hasShownAlcoholWarning.value) {
        WarningDialog(
            onDismiss = {
                showAlcoholWarningDialog = false
                authViewModel.hasShownAlcoholWarning.value = true
            }
        )
    }
}
