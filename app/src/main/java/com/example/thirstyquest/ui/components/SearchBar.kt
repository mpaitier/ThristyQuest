package com.example.thirstyquest.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getAllUsersExcept
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//////////////////////////////////////////////////////////////////////////////////
//                                Composables
// ------------------------------ Research section ------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchQuery: String, onQueryChange: (String) -> Unit)
{
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
fun SearchResultsList(query: String, navController: NavController, authViewModel: AuthViewModel)
{
    val users = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    val currentUserUid by authViewModel.uid.observeAsState()
    var usersToShowCount by remember { mutableIntStateOf(10) } // Limit users to show

    LaunchedEffect(query) {
        usersToShowCount = 10 // for each query, reset the limit

        currentUserUid?.let { uid ->
            getAllUsersExcept(uid) { userList ->
                users.value = userList
                    .mapNotNull { user ->
                        val uid = user["uid"] as? String
                        val name = user["name"] as? String
                        if (uid != null && name != null && uid != currentUserUid.toString()) Pair(uid, name) else null
                    }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(4.dp)
    ) {
        val filteredUsers = users.value.filter { it.second.contains(query, ignoreCase = true) }

        if (filteredUsers.isEmpty()) {
            Text(text = "Aucun utilisateur trouvé.")
        } else {
            val usersToDisplay = filteredUsers.take(usersToShowCount)

            usersToDisplay.forEach { user ->
                SearchResultsItem(
                    uid = user.first,
                    userName = user.second,
                    query = query,
                    navController = navController,
                    authViewModel = authViewModel
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Button "Show more"
            if (usersToShowCount < filteredUsers.size) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { usersToShowCount *= 2 },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(
                        stringResource(R.string.show_more),
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun SearchResultsItem(uid:String, userName:String, query: String, navController: NavController, authViewModel: AuthViewModel)
{
    val interactionSource = remember { MutableInteractionSource() }
    var photoUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uid) {
        // Get profile picture
        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .await()
        photoUrl = snapshot.getString("photoUrl")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(interactionSource = interactionSource, indication = null)
                {
                    navController.navigate(Screen.FriendProfile.name + "/$uid")
                }
    ) {
        // Profile picture
        if (!photoUrl.isNullOrEmpty()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Friend profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.pdp),
                contentDescription = "Friend profile default picture",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }
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
