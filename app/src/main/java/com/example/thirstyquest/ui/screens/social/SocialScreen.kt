package com.example.thirstyquest.ui.screens.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.window.Dialog
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.screens.AddDrinkDialog
import com.example.thirstyquest.ui.screens.ItemBoisson

@Composable
fun SocialScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var showCreateLeagueDialog by remember { mutableStateOf(false) }

    val friendNumber = 13 // TODO : replace with actual user's friends count
    val leagueNumber = 7 // TODO : replace with actual user's leagues count

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp, 8.dp, 16.dp, 4.dp)
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        SearchBar()

        Spacer(modifier = Modifier.height(12.dp))

        // League section
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
                onClick = { showDialog = true },
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
        LeagueList(navController, leagueNumber)

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.friends) + " ($friendNumber)",
            fontSize = 20.sp,
            color = primaryColor
        )
        Spacer(modifier = Modifier.height(12.dp))
        FriendsList(navController, friendNumber)
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

                val newLeagueID = addToLeagueList(leagueCode)
                navController.navigate(Screen.LeagueContent.name + "/$newLeagueID")
            }
        )
    }

    // Create a league dialog
    if (showCreateLeagueDialog) {
        CreateLeagueDialog(
            onDismiss = { showCreateLeagueDialog = false },
            onValidate = { leagueName ->
                showCreateLeagueDialog = false

                val newLeagueID = createLeague(leagueName)
                navController.navigate(Screen.LeagueContent.name + "/$newLeagueID")
            }
        )
    }
}

fun createLeague(leagueName: String): Int
{
    // TODO : Create a new league with values
    return 69 // Random value
}

fun addToLeagueList(leagueCode: String): Int
{
    // TODO : Add league to user's list
    return 69 // Random value
}

//////////////////////////////////////////////////////////////////////////////////
//                                Composables
// ------------------------------ Research section ------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar()
{
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val outlineColor = MaterialTheme.colorScheme.outline
    val backgroundColor = MaterialTheme.colorScheme.background

    var searchQuery by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape = CircleShape),
        leadingIcon = {
            Icon(
                tint = tertiaryColor,
                imageVector = Icons.Filled.Search,
                contentDescription = "Rechercher"
            )
        },
        placeholder = {
            Text(text = stringResource(id = R.string.research))
        },
        shape = CircleShape,
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = outlineColor,
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = secondaryColor,
            containerColor = backgroundColor
        )
    )
}

// --------------------------------- League List ---------------------------------

@Composable
fun LeagueList(
    navController: NavController,
    leagueNumber: Int
)
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((0.26f*LocalConfiguration.current.screenHeightDp).dp)
            .verticalScroll(rememberScrollState()) // Enable manual scroll
    ) {
        Column {
            repeat(leagueNumber) { index ->
                LeagueItem(
                    navController = navController,
                    leagueID = index    // TODO : get the league ID
                )
            }
        }
    }
}


@Composable
fun LeagueItem(
    navController: NavController,
    leagueID: Int
)
{
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val tertiaryColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)

    val peopleCount = 26                    // TODO : get the number of people in the league with the leagueID
    val rank = 8                            // TODO : get the rank of the user in the league with the leagueID
    val leagueName = "Ligue $leagueID"      // TODO : get the name of the league with the leagueID

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPressed) tertiaryColor else Color.Transparent)
            .clickable (
                interactionSource = interactionSource,
                indication = null
            ) {
                navController.navigate(Screen.LeagueContent.name + "/$leagueID")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: get league image------------------------------------------------------------------------------------
        Image(
            painter = painterResource(id = R.drawable.league_logo),
            contentDescription = "Profil",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = leagueName,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "$peopleCount",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Person Icon",
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = "$rank",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Star Icon",
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

// --------------------------------- Friends List ---------------------------------

@Composable
fun FriendsList(
    navController: NavController,
    friendNumber: Int
)
{
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 colonnes
        contentPadding = PaddingValues(start = 20.dp, top = 15.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        items(friendNumber) { friendID ->
            FriendItem(
                navController= navController,
                friendID = friendID
            )
        }
    }

}

// --------------------------------- Friends Item ---------------------------------

@Composable
fun FriendItem(
    navController: NavController,
    friendID: Int,
    modifier: Modifier = Modifier
)
{
    val interactionSource = remember { MutableInteractionSource() }

    val friendName = "Ami $friendID" // TODO : get the friend name
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .padding(8.dp)
    ) {
        // TODO : get friend profile picture
        if (friendID == -1) {
            Spacer(modifier = Modifier.size(60.dp))
        }
        else
        {
            Column (
                modifier = Modifier.clickable (interactionSource = interactionSource, indication = null)
                {
                    navController.navigate(Screen.LeagueContent.name + "/$friendID")    // TODO : navigate to friend profile
                }
            ) {
               Image(
                    painter = painterResource(id = R.drawable.pdp),
                    contentDescription = "Friend Icon",
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.CenterHorizontally),
                )

                Text(
                    text = friendName,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis, // If name is too long, it will be cut
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }

    }
}
///////////////////////////////////// Dialog /////////////////////////////////////
// --------------------------------- Add League PopUp ---------------------------------

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

                Divider(color = MaterialTheme.colorScheme.onBackground)

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

// --------------------------------- Add League PopUp ---------------------------------

@Composable
fun CreateLeagueDialog(
    onDismiss: () -> Unit,
    onValidate: (String) -> Unit
) {
    var leagueName by remember { mutableStateOf("") }

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
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(140.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {

                    }

                    // Add picture button
                    IconButton(
                        onClick = { /* TODO: Add picture selection */ },
                        modifier = Modifier
                            .size(48.dp)
                            .offset(x = 8.dp, y = 8.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircleOutline,
                            contentDescription = "Add picture",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // League's name entry
                OutlinedTextField(
                    value = leagueName,
                    onValueChange = { leagueName = it },
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
                        onClick = { if (leagueName.isNotBlank()) onValidate(leagueName) },  // TODO : Add picture save
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

//////////////////////////////////////////////////////////////////////////////////
//                               Previews

@PreviewScreenSizes
@Composable
fun SocialScreenPreview() {
    SocialScreen(rememberNavController())
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar()
}

@Preview
@Composable
fun LeagueItemPreview() {
    LeagueItem(navController = rememberNavController(), 26)
}

@Preview
@Composable
fun FriendItemPreview() {
    FriendItem(navController = rememberNavController(),26)
}
