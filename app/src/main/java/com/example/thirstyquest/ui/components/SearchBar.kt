package com.example.thirstyquest.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getAllFollowingIdCoroutine
import com.example.thirstyquest.db.getAllUsersExcept
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.viewmodel.AuthViewModel

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
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Effacer la recherche"
                    )
                }
            }
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

// --------------------------------- Search results ---------------------------------
@Composable
fun SearchResultsList(query: String, navController: NavController, authViewModel: AuthViewModel) {
    val users = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    val currentUserUid by authViewModel.uid.observeAsState()

    LaunchedEffect(query) {
        currentUserUid?.let { uid ->
            getAllUsersExcept(uid) { userList ->
                users.value = userList
                    .mapNotNull { user ->
                        val uid = user["uid"] as? String
                        val name = user["name"] as? String
                        if (uid != null && name != null && uid != currentUserUid.toString()) Pair(uid, name) else null
                    }
            }                                                    // TODO : replace with actual user's friends count
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(4.dp)
    ) {
        val filteredUsers = users.value.filter { it.second.contains(query, ignoreCase = true) }

        if (filteredUsers.isEmpty()) {
            Text(text = "Aucun utilisateur trouvé.")
        } else {
            filteredUsers.forEach { user ->
                SearchResultsItem(uid = user.first, userName = user.second, query = query, navController = navController, authViewModel = authViewModel)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}




@Composable
fun SearchResultsItem(uid:String, userName:String, query: String, navController: NavController, authViewModel: AuthViewModel) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(interactionSource = interactionSource, indication = null)
                {
                    navController.navigate(Screen.FriendProfile.name + "/$uid")              // TODO : navigate to friend profile
                }
    ) {
        // Profile picture
        Image(
            painter = painterResource(id = R.drawable.pdp),
            contentDescription = "Profil",
            modifier = Modifier
                .size(50.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // In user's name, query is in bold
        val annotatedName = buildAnnotatedString {
            val startIndex = userName.indexOf(query, ignoreCase = true)
            if (startIndex != -1) {
                append(userName.substring(0, startIndex))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(userName.substring(startIndex, startIndex + query.length))  // query is bold
                }
                append(userName.substring(startIndex + query.length))
            } else {
                append(userName)
            }
        }
        Text(
            text = annotatedName,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1F))
        // Friend button
        AddFriendButton(uid, authViewModel)
    }
}





/*
@Composable

fun SearchResultsList(query: String,authViewModel: AuthViewModel) {

    // Filter user's research
    val filteredUsers = userList.filter { it.name.contains(query, ignoreCase = true) }
    val testList : List<Pair<String, String>> = listOf(
        Pair("1", "Alex"),
        Pair("2", "Alexa"),
        Pair("3", "Alexander"),
        Pair("4", "Alexis"),
        Pair("5", "Ben"),
    )
    val pairFiltredUsers = testList.filter { it.second.contains(query, ignoreCase = true) }

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
        pairFiltredUsers.forEach { user ->
            SearchResultsItem(userID = user.first, userName = user.second, query = query,authViewModel = authViewModel)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}




// --------------------------------- Search results ---------------------------------
@Composable
fun SearchResultsItem(userID: String, userName: String, query: String,authViewModel: AuthViewModel) {
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
            val startIndex = userName.indexOf(query, ignoreCase = true)
            if (startIndex != -1) {
                append(userName.substring(0, startIndex))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(userName.substring(startIndex, startIndex + query.length))  // query is bold
                }
                append(userName.substring(startIndex + query.length))
            } else {
                append(userName)
            }
        }
        Text(
            text = annotatedName,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1F))
        //AddFriendButton(friendId = userID,authViewModel = authViewModel)

    }
}*/

