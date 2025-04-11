package com.example.thirstyquest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.WineBar
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
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.example.thirstyquest.db.DrinkPointManager.getAllDrinksFromFirestore
import com.example.thirstyquest.db.DrinkPointManager.getTopDrinksFromFirestore
import com.example.thirstyquest.db.getUserLastPublications
import com.example.thirstyquest.ui.dialog.AllDrinksDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch


@Composable
fun MainMenuScreen(authViewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current
    val userId by authViewModel.uid.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var selectedPublication by remember { mutableStateOf<Publication?>(null) }
    var publications by remember { mutableStateOf<List<Publication>>(emptyList()) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var showAllDrinksDialog by remember { mutableStateOf(false) }
    var topDrinks by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var allDrinks by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    // Charger les points des boissons
    LaunchedEffect(Unit) {
        topDrinks = getTopDrinksFromFirestore()
        allDrinks = getAllDrinksFromFirestore()
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { result: Bitmap? ->
        if (result != null) {
            capturedImage = result
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
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Section TOP DRINKS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAllDrinksDialog = true }
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.top_drinks),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                topDrinks.forEach { (name, points) ->
                    val icon = when (name) {
                        "Bière" -> Icons.Filled.LocalDrink
                        "Vin" -> Icons.Filled.WineBar
                        "Cocktail" -> Icons.Filled.LocalBar
                        "Shot" -> Icons.Filled.CameraAlt
                        else -> Icons.Filled.LocalDrink
                    }
                    TopDrinkItem(icon, name, "$points points")
                }
            }


            Spacer(modifier = Modifier.height(32.dp))

            // Bouton Ajouter une conso
            Button(
                onClick = {
                    if (userId != null) takePictureLauncher.launch(null)
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

    //Dialog pour ajout de publication
    if (showDialog && userId != null) {
        AddPublicationDialog(
            userId = userId!!,
            imageBitmap = capturedImage,
            onDismiss = {
                showDialog = false
                capturedImage = null
            },
            onSuccess = {
                coroutineScope.launch {
                    Toast.makeText(context, "Publication ajoutée avec succès", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Dialog publication détail
    selectedPublication?.let {
         PublicationDetailDialog(publication = it, onDismiss = { selectedPublication = null })
    }

    // Dialog avec toutes les boissons
    if (showAllDrinksDialog) {
        AllDrinksDialog(
            drinks = allDrinks,
            onDismiss = { showAllDrinksDialog = false }
        )
    }
}



@Composable
fun TopDrinkItem(icon: androidx.compose.ui.graphics.vector.ImageVector, name: String, points: String)
{
    // TODO : function takes top drink items in data base, we shouldn't need to declare them manually
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = name,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = name, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(text = points, fontSize = 14.sp, color = Color.Gray)
    }
}


