package com.example.thirstyquest.ui.screens.social

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalContext
import com.example.thirstyquest.R
import com.example.thirstyquest.db.addLeagueToFirestore
import com.example.thirstyquest.db.getAllUserLeaguesIdCoroutine
import com.example.thirstyquest.db.joinLeagueIfExists
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.FollowList
import com.example.thirstyquest.ui.components.LeagueList
import com.example.thirstyquest.ui.components.SearchBar
import com.example.thirstyquest.ui.components.SearchResultsList
import com.example.thirstyquest.ui.dialog.AddLeagueDialog
import com.example.thirstyquest.ui.dialog.CreateLeagueDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.DisposableEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.thirstyquest.db.getAllfollowingIdSnap
import com.example.thirstyquest.ui.dialog.createImageFile
import com.example.thirstyquest.ui.dialog.getActivity
import com.example.thirstyquest.ui.dialog.trySendNotification


@Composable
fun SocialScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var showCreateLeagueDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // State pour stocker dynamiquement le nombre d'amis
    var leagueNumber by remember { mutableIntStateOf(0) }
    var friendsList by remember { mutableStateOf<List<String>>(emptyList()) }
    var leagueList by remember { mutableStateOf<List<String>>(emptyList()) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val activity = getActivity()
    val imageFile = remember { createImageFile(context) }
    val imageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri = imageUri
            showCreateLeagueDialog = true
        }
    }

    val currentUserUid by authViewModel.uid.observeAsState()


    DisposableEffect(currentUserUid) {
        val uid = currentUserUid
        if (uid != null) {
            getAllfollowingIdSnap(uid) { updatedList ->
                friendsList = updatedList
            }
        }
        onDispose {  }
    }
    LaunchedEffect(currentUserUid) {
        currentUserUid?.let { uid ->
            leagueList = getAllUserLeaguesIdCoroutine(uid)
            leagueNumber = leagueList.size
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp, 8.dp, 16.dp, 4.dp)
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary

        SearchBar(searchQuery, onQueryChange = { searchQuery = it })

        Spacer(modifier = Modifier.height(12.dp))

        // If no user's entry, show user's league & friends
        if (searchQuery.isBlank()) {
            // League list
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.leagues) + " ($leagueNumber)",
                    fontSize = 20.sp,
                    color = primaryColor
                )

                IconButton(
                    onClick = {showDialog = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            activity?.let {
                                ActivityCompat.requestPermissions(
                                    it,
                                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                    1001
                                )
                            }
                        } else {
                            trySendNotification(context, "Titre", "tu viens de rentrer dans une league")
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircleOutline,
                        contentDescription = "Ajouter une ligue",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LeagueList(navController, authViewModel)
            // Friend list
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.friends) + " (${friendsList.size})",
                fontSize = 20.sp,
                color = primaryColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            FollowList(navController, authViewModel)
        }
        else {
            SearchResultsList(searchQuery, navController, authViewModel)
        }
    }

    // Add league dialog
    if (showDialog) {
        AddLeagueDialog(
            onDismiss = { showDialog = false },
            onCreateLeague = {
                showDialog = false
                showCreateLeagueDialog = true
            },
            onJoinLeague = { leagueCode ->
                showDialog = false

                currentUserUid?.let { uid ->
                    joinLeagueIfExists(
                        uid = uid,
                        leagueCode = leagueCode,
                        context = context,
                        navController = navController
                    )
                }
            }
        )
    }

    // Create a league dialog
    if (showCreateLeagueDialog) {
        CreateLeagueDialog(
            onDismiss = { showCreateLeagueDialog = false },
            onValidate = { leagueName, imageUri ->
                showCreateLeagueDialog = false

                currentUserUid?.let { uid ->
                    addLeagueToFirestore(
                        uid = uid,
                        name = leagueName,
                        imageUri = imageUri,
                        context = context
                    ) { newLeagueID ->
                        navController.navigate(Screen.LeagueContent.name + "/$newLeagueID")
                    }
                }
            }
        )

    }
}
