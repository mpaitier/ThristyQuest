package com.example.thirstyquest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import com.example.thirstyquest.db.getLeagueName
import com.example.thirstyquest.db.getLeagueOwnerId
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
    var showImageFullscreen by remember { mutableStateOf(false) }

    val currentBackStackEntry = navController.currentBackStackEntryAsState()

    var photoUrl by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) } // Ajouté pour forcer le rafraîchissement
    val uid by authViewModel.uid.observeAsState()

    // Recharge la photo à chaque changement de refreshKey
    LaunchedEffect(uid, refreshKey) {
        uid?.let { userId ->
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .await()

            photoUrl = snapshot.getString("photoUrl")
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
            IconButton(onClick =
                {
                    if (currentBackStackEntry.value?.destination?.route != Screen.Profile.name) {
                        navController.navigate(Screen.Login.name)
                    }
                    else {
                        showImageFullscreen = true
                    }
                }
            )
            {
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

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    if (showImageFullscreen) {
        Dialog(onDismissRequest = { showImageFullscreen = false }) {
            Box(
                modifier = Modifier.height(screenHeight/2)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "League image fullscreen",
                    modifier = Modifier
                        .padding(32.dp)
                        .height((screenHeight/2)-20.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }

    // Ouvre le Dialog d’édition de profil
    if (showDialog) {
        EditProfileDialog(
            authViewModel = authViewModel,
            onDismiss = { showDialog = false },
            onPhotoUpdated = { refreshKey++ }
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
                    .size((0.04166* LocalConfiguration.current.screenHeightDp).dp)
                    .clip(CircleShape)
                    .clickable { showImageFullscreen = true }, // Clic pour afficher
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.league_logo),
                contentDescription = "Profil",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
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

        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        if (showImageFullscreen) {
            Dialog(onDismissRequest = { showImageFullscreen = false }) {
                Box(
                    modifier = Modifier.height(screenHeight/2)
                ) {
                    AsyncImage(
                        model = leaguePhotoUrl,
                        contentDescription = "League image fullscreen",
                        modifier = Modifier
                            .padding(32.dp)
                            .height((screenHeight/2)-20.dp)
                            .align(Alignment.Center)
                    )
                }
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
            leagueID = leagueID,
            leaguePhotoUrl = leaguePhotoUrl.toString()
        )
    }
}
