package com.example.thirstyquest.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.thirstyquest.R
import com.example.thirstyquest.db.checkIfFriend
import com.example.thirstyquest.db.getAllFollowingIdCoroutine
import com.example.thirstyquest.db.getUserName
import com.example.thirstyquest.db.getUserNameCoroutine
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

// --------------------------------- Friends List ---------------------------------

@Composable
fun FriendsList(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var friendsList by remember { mutableStateOf<List<String>>(emptyList()) }
    val currentUserUid by authViewModel.uid.observeAsState()

    LaunchedEffect(currentUserUid) {
        currentUserUid?.let { uid ->
            friendsList = getAllFollowingIdCoroutine(uid)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(start = 20.dp, top = 15.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        items(friendsList) { friendID ->
            FriendItem(
                navController = navController,
                friendID = friendID
            )
        }
    }
}

// --------------------------------- Friends Item ---------------------------------

@Composable
fun FriendItem(
    navController: NavController,
    friendID: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var friendName by remember { mutableStateOf("") }

    // Récupération du nom dans un `LaunchedEffect`
    LaunchedEffect(friendID) {
        friendName = getUserNameCoroutine(friendID)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.clickable(interactionSource = interactionSource, indication = null) {
                navController.navigate(Screen.FriendProfile.name + "/$friendID")
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
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


//////////////////////////////////////////////////////////////////////////////////
//                               Previews

/*@Preview
@Composable
fun FriendItemPreview() {
    FriendItem(navController = rememberNavController(),)
}*/