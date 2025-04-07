package com.example.thirstyquest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.thirstyquest.R
import coil.compose.AsyncImage
import com.example.thirstyquest.db.getUserProfileImageUrl
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import com.example.thirstyquest.db.getLeagueName
import com.example.thirstyquest.db.getLeagueOwnerId
import com.example.thirstyquest.db.getUserProfileImageUrl
import com.example.thirstyquest.db.updateLeagueName
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.dialog.EditProfileDialog
import com.example.thirstyquest.ui.dialog.LeagueEditDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, authViewModel: AuthViewModel) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    var showDialog by remember { mutableStateOf(false) }
    val currentBackStackEntry = navController.currentBackStackEntryAsState()

    var photoUrl by remember { mutableStateOf<String?>(null) }
    val uid by authViewModel.uid.observeAsState()

    LaunchedEffect(uid) {
        uid?.let { userId ->
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        photoUrl = document.getString("photoUrl")
                    }
                }
        }
    }


    CenterAlignedTopAppBar(
        title = {
            Text(
                modifier = Modifier.clickable {
                    navController.navigate(Screen.MainMenu.name)
                },
                text = stringResource(id = R.string.app_name),
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                color = primaryColor
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigate(Screen.Login.name) }) {
                if (!photoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Photo de profil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.pdp),
                        contentDescription = "Profil",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                }
            }

        },
        actions = {
            if (currentBackStackEntry.value?.destination?.route == Screen.Profile.name) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Modifier"
                    )
                }
                IconButton(onClick = { navController.navigate(Screen.Settings.name) }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Paramètres"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
        )
    )

    if (showDialog) {
        EditProfileDialog(
            authViewModel = authViewModel,
            onDismiss = { showDialog = false }
        )
    }
}

// ------------------------------ League Top Bar ------------------------------
@Composable
fun LeagueTopBar(navController: NavController, authViewModel: AuthViewModel, leagueID: String) {
    var showDialog by remember { mutableStateOf(false) }

    var leagueName by remember { mutableStateOf("") }
    var leagueOwnerId by remember { mutableStateOf("") }
    var leaguePhotoUrl by remember { mutableStateOf<String?>(null) }

    val currentUserUid by authViewModel.uid.observeAsState()
    LaunchedEffect(currentUserUid) {
        currentUserUid?.let { uid ->
            leagueName = getLeagueName(leagueID)
            leagueOwnerId = getLeagueOwnerId(leagueID)

            val snapshot = FirebaseFirestore.getInstance()
                .collection("leagues")
                .document(leagueID)
                .get()
                .await()

            leaguePhotoUrl = snapshot.getString("photoUrl")
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height((0.04166* LocalConfiguration.current.screenHeightDp).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        var showImageFullscreen by remember { mutableStateOf(false) }

        if (!leaguePhotoUrl.isNullOrEmpty()) {
            AsyncImage(
                model = leaguePhotoUrl,
                contentDescription = "League image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable { showImageFullscreen = true }, // 👈 Clic pour afficher
                contentScale = ContentScale.Inside
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Default league icon",
                modifier = Modifier.size(60.dp)
            )
        }

        if (showImageFullscreen) {
            Dialog(onDismissRequest = { showImageFullscreen = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = leaguePhotoUrl,
                        contentDescription = "League image fullscreen",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                            .align(Alignment.Center)
                    )
                    IconButton(
                        onClick = { showImageFullscreen = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White
                        )
                    }
                }
            }
        }
        else {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Default league icon",
                modifier = Modifier.size(60.dp)
            )
        }


        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = leagueName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        // TODO : navigate to league settings & visible only if user is owner
        if(currentUserUid == leagueOwnerId) {
            IconButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Modifier")
            }
        }
    }

    if (showDialog) {
        LeagueEditDialog(
            onDismiss = { showDialog = false },
            onValidate = { newLeagueName ->
                showDialog = false
                updateLeagueName(leagueID, newLeagueName)
                leagueName = newLeagueName
                // TODO : modify league's picture
            },
            leagueID = leagueID
        )
    }
}
