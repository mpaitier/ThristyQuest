package com.example.thirstyquest.ui.screens.social

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getDrawable
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.Member
import com.example.thirstyquest.ui.screens.AddDrinkDialog
import com.example.thirstyquest.ui.screens.ItemBoisson
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.delay

@Composable
fun SocialScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var showCreateLeagueDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val friendNumber = 13                                                                           // TODO : replace with actual user's friends count
    val leagueNumber = 7                                                                            // TODO : replace with actual user's leagues count

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
            // Friend list
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.friends) + " ($friendNumber)",
                fontSize = 20.sp,
                color = primaryColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            FriendsList(navController, friendNumber)
        }
        // If searching, show matching people
        else {
            SearchResultsList(searchQuery)
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
fun SearchBar(searchQuery: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape = CircleShape),
        leadingIcon = {
            Icon(
                tint = MaterialTheme.colorScheme.tertiary,
                imageVector = Icons.Filled.Search,
                contentDescription = "Rechercher"
            )
        },
        placeholder = { Text(text = stringResource(id = R.string.research)) },
        shape = CircleShape,
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.outline,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor =  MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.background
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
            contentDescription = "Image de ligue",
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

    val friendName = "Ami $friendID"                                                                // TODO : get the friend name
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .padding(8.dp)
    ) {
                                                                                                    // TODO : get friend profile picture
        Column (
            modifier = Modifier.clickable(interactionSource = interactionSource, indication = null)
            {
                navController.navigate(Screen.LeagueContent.name + "/$friendID")              // TODO : navigate to friend profile
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

// --------------------------------- Search results ---------------------------------
data class Users(val ID: Int, val name: String, val level: Int, val isFriend: Boolean)
@Composable
fun SearchResultsList(query: String) {
    val userList = listOf(
        Users(ID = 1, name = "Alex", level = 12, isFriend = true),
        Users(ID = 2, name = "Alexa", level = 8, isFriend = false),
        Users(ID = 3, name = "Alexander", level = 15, isFriend = true),
        Users(ID = 4, name = "Alexis", level = 5, isFriend = false),
        Users(ID = 5, name = "Ben", level = 20, isFriend = true),
        Users(ID = 6, name = "Benjamin", level = 3, isFriend = false),
        Users(ID = 7, name = "Benedict", level = 11, isFriend = true),
        Users(ID = 8, name = "Benny", level = 7, isFriend = false),
        Users(ID = 9, name = "Charlie", level = 14, isFriend = true),
        Users(ID = 10, name = "Charlotte", level = 9, isFriend = false),
        Users(ID = 11, name = "Charlene", level = 17, isFriend = true),
        Users(ID = 12, name = "Charles", level = 6, isFriend = false),
        Users(ID = 13, name = "Dan", level = 19, isFriend = true),
        Users(ID = 14, name = "Daniel", level = 4, isFriend = false),
        Users(ID = 15, name = "Danielle", level = 13, isFriend = true),
        Users(ID = 16, name = "Danny", level = 10, isFriend = false),
        Users(ID = 17, name = "Eli", level = 16, isFriend = true),
        Users(ID = 18, name = "Elijah", level = 5, isFriend = false),
        Users(ID = 19, name = "Elisa", level = 21, isFriend = true),
        Users(ID = 20, name = "Elise", level = 2, isFriend = false),
        Users(ID = 21, name = "Sam", level = 18, isFriend = true),
        Users(ID = 22, name = "Samuel", level = 7, isFriend = false),
        Users(ID = 23, name = "Samantha", level = 22, isFriend = true),
        Users(ID = 24, name = "Samson", level = 1, isFriend = false),
        Users(ID = 26, name = "Lulu", level = 100, isFriend = true)
    )

    // Filter user's research
    val filteredUsers = userList.filter { it.name.contains(query, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(
            text = "Résultats pour \"$query\"",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        filteredUsers.forEach { user ->
            SearchResultsItem(user = user, query = query)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// --------------------------------- Search results ---------------------------------
@Composable
fun SearchResultsItem(user: Users, query: String) {
    var isFriend by remember { mutableStateOf(user.isFriend) } // to change isFriend value with buttons
    var isAnimating by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.pdp),
            contentDescription = "Profil",
            modifier = Modifier
                .size(50.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // In user's name, query is in bold
        val annotatedName = buildAnnotatedString {
            val startIndex = user.name.indexOf(query, ignoreCase = true)
            if (startIndex != -1) {
                append(user.name.substring(0, startIndex))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(user.name.substring(startIndex, startIndex + query.length))  // query is bold
                }
                append(user.name.substring(startIndex + query.length))
            } else {
                append(user.name)
            }
        }
        Text(
            text = annotatedName,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1F))

        IconButton(
            onClick = {
                focusManager.clearFocus()
                if (!isAnimating) {  // Disable mulitple click during animation
                    isAnimating = true
                }
            },
            modifier = Modifier.size(45.dp)
        ) {
            when {
                isAnimating && !isFriend -> {           // filling animation
                    Image(
                        painter = rememberDrawablePainter(
                            drawable = getDrawable(
                                LocalContext.current,
                                R.drawable.anim_beer_filling
                            )
                        ),
                        contentDescription = "Filling beer",
                        modifier = Modifier
                            .size(40.dp)
                            .fillMaxSize()
                    )
                    LaunchedEffect(Unit) {
                        delay(1000)
                        isAnimating = false
                        isFriend = true
                        Toast.makeText(
                            context,
                            "${user.name} ${context.getString(R.string.friend_added)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                isAnimating && isFriend -> {            // emptying animation
                    Image(
                        painter = rememberDrawablePainter(
                            drawable = getDrawable(
                                LocalContext.current,
                                R.drawable.anim_beer_emptying
                            )
                        ),
                        contentDescription = "Emptying beer",
                        modifier = Modifier
                            .size(40.dp)
                            .fillMaxSize()
                    )
                    LaunchedEffect(Unit) {
                        delay(1000)
                        isAnimating = false
                        isFriend = false
                        Toast.makeText(
                            context,
                            "${user.name} ${context.getString(R.string.friend_deleted)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                isFriend -> { // show full glass
                    Image(
                        painter = painterResource(id = R.drawable.icon_beer_full),
                        contentDescription = "Beer full",
                        modifier = Modifier
                            .size(40.dp)
                            .fillMaxSize()
                    )
                }

                else -> {   // show empty glass
                    Image(
                        painter = painterResource(id = R.drawable.icon_beer_empty),
                        contentDescription = "Beer empty",
                        modifier = Modifier
                            .size(40.dp)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

///////////////////////////////////// Dialog /////////////////////////////////////
// --------------------------------- Add League Dialog ---------------------------------

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

// --------------------------------- Add League Dialog ---------------------------------

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
                AddPicture()

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

@Composable
fun AddPicture() {                                                                                  // TODO : ajouter l'image enregistré dans la DB
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
fun LeagueItemPreview() {
    LeagueItem(navController = rememberNavController(), 26)
}

@Preview
@Composable
fun FriendItemPreview() {
    FriendItem(navController = rememberNavController(),26)
}
