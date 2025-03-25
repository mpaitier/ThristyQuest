package com.example.thirstyquest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getAllFollowingIdCoroutine
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.dialog.EditProfileDialog
import com.example.thirstyquest.ui.dialog.LeagueEditDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, authViewModel: AuthViewModel) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    var showDialog by remember { mutableStateOf(false) }
    val currentBackStackEntry = navController.currentBackStackEntryAsState()

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
                Image(
                    painter = painterResource(id = R.drawable.pdp),
                    contentDescription = "Profil",
                    modifier = Modifier.size(60.dp)
                )
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
fun LeagueTopBar(navController: NavController, leagueID: Int) {
    var showDialog by remember { mutableStateOf(false) }

    val leagueName = "Ligue $leagueID"          // TODO : get league name with leagueID
    val leagueOwnerId = 2                       // TODO : get league's owner's ID  with leagueID
    val ownID = leagueID                        // TODO : get own ID

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

        Icon(imageVector = Icons.Filled.Menu,       // TODO : replace by league picture
            contentDescription = "League picture",
            modifier = Modifier.size(60.dp))

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = leagueName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        // TODO : navigate to league settings & visible only if user is owner
        if(ownID == leagueOwnerId) {
            IconButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Modifier")
            }
        }
    }

    if (showDialog) {
        LeagueEditDialog(
            onDismiss = { showDialog = false },
            onValidate = { leagueName ->
                showDialog = false
                // TODO : modify league's name
                // TODO : modify league's picture
            },
            leagueID = leagueID
        )
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//      Preview

@Preview(showBackground = true)
@Composable
fun PreviewLeagueTopBar() {
    LeagueTopBar(navController = rememberNavController(), leagueID = 12)
}
