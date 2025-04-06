package com.example.thirstyquest.ui.screens

import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.example.thirstyquest.ui.dialog.PublicationDetailDialog
import com.example.thirstyquest.data.PublicationHist
import com.example.thirstyquest.db.getAverageDrinkConsumption
import com.example.thirstyquest.db.getPublicationCountByCategory
import com.example.thirstyquest.db.getTotalDrinkVolume
import com.example.thirstyquest.db.getTotalMoneySpent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.thirstyquest.db.fetchUserPublications
import com.example.thirstyquest.ui.viewmodel.AuthViewModel


@Composable
fun MainMenuScreen(authViewModel: AuthViewModel, navController: NavController) {
    val userId by authViewModel.uid.observeAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedPublication by remember { mutableStateOf<Publication?>(null) }
    var descriptions by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var publications by remember { mutableStateOf<List<Publication>>(emptyList()) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { result: Bitmap? ->
        if (result != null) {
            capturedImage = result
            showDialog = true
        }
    }


    LaunchedEffect(userId) {
        userId?.let { uid ->
            fetchUserPublications(uid) { newList ->
                publications = newList
            }

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show 3 best drinks
            Text(stringResource(id = R.string.top_drinks), fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TopDrinkItem(Icons.Filled.WineBar, "Vin rouge", "3 000 points")
                TopDrinkItem(Icons.Filled.LocalDrink, "Bière rouge", "2 800 points")
                TopDrinkItem(Icons.Filled.LocalBar, "Gin Tonic", "2 600 points")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bouton Ajouter une consommation
            Button(
                onClick = {
                    if (userId != null) {
                        takePictureLauncher.launch(null)
                        // showDialog = true // Ouvrir la boîte de dialogue pour ajouter une publication
                    } else {
                        navController.navigate("login") // Rediriger vers l'écran de connexion
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(100.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = stringResource(id = R.string.add_drink))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.add_drink), fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

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
                            modifier = Modifier.fillMaxWidth(),
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
    }

    if (showDialog && userId != null) {
        AddPublicationDialog(
            userId = userId!!,
            onDismiss = {
                showDialog = false
                capturedImage = null
            },
            imageBitmap = capturedImage // ← passe l'image au composable
        )
    }


    selectedPublication?.let { //TODO : faire le pop up du click en dynamique
        PublicationDetailDialog(publication = it, onDismiss = { selectedPublication = null })
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
