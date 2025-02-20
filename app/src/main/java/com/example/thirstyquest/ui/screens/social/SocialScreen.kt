package com.example.thirstyquest.ui.screens.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen

@Composable
fun SocialScreen(navController: NavController) {
    val friendNumber = 13 // TODO : replace 13 by the user's friends count
    val leagueNumber = 7 // TODO : replace 7 by the user's leagues count
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp, 8.dp, 16.dp, 4.dp)

    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        SearchBar()

        Spacer(modifier = Modifier.height(12.dp))
        // TODO : Row & Icon to add a new league
        Text(
            text = stringResource(id = R.string.leagues) + " (" + leagueNumber + ")",
            fontSize = 20.sp,
            color = primaryColor
        )
        Spacer(modifier = Modifier.height(12.dp))
        LeagueList(navController, leagueNumber)

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.friends) + " (" + friendNumber + ")",
            fontSize = 20.sp,
            color = primaryColor
        )
        Spacer(modifier = Modifier.height(12.dp))
        FriendsList(navController, friendNumber)
    }
}


//////////////////////////////////////////////////////////////////////////////////
//                                Composables
// ------------------------------ Research section ------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
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
fun LeagueList(navController: NavController, leagueNumber: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((0.23f*LocalConfiguration.current.screenHeightDp).dp)
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
fun LeagueItem(navController: NavController, leagueID: Int) {
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
            modifier = Modifier.size(30.dp)
        )

        Text(
            text = "$rank",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Person Icon",
            modifier = Modifier.size(30.dp)
        )
    }
}

// --------------------------------- Friends List ---------------------------------

@Composable
fun FriendsList(navController: NavController, friendNumber: Int) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ){
        items((friendNumber/3)+1) { index -> // TODO : get the user's friends informations
            LazyRow (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                items(3) { index2 ->
                    if(index * 3 + index2 < friendNumber) {
                        FriendItem(
                            navController= navController,
                            friendID = index * 3 + index2 + 1
                        )
                    }
                    else {
                        FriendItem(
                            navController= navController,
                            friendID = -1
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun FriendItem(navController: NavController, friendID: Int,  modifier: Modifier = Modifier) {
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
