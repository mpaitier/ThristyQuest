package com.example.thirstyquest.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getDrawable
import com.example.thirstyquest.R
import com.example.thirstyquest.db.checkIfFollowing
import com.example.thirstyquest.db.getUserNameById
import com.example.thirstyquest.db.toggleFollow
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.delay

@Composable
fun AddFriendButton(friendId: String, authViewModel: AuthViewModel) {
    val currentUserUid by authViewModel.uid.observeAsState()
    var isFriend by remember { mutableStateOf(false) }
    var isAnimating by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var friendName by remember { mutableStateOf<String?>(null) }
    // Check if the friend is already followed by the user
    LaunchedEffect(currentUserUid) {
        currentUserUid?.let { uid ->
            checkIfFollowing(uid, friendId) { isFriend = it }
        }
    }
    // Fetch the friend name based on their friendId
    LaunchedEffect(friendId) {
        getUserNameById(friendId) { name ->
            friendName = name
        }
    }
    IconButton(
        onClick = {
            focusManager.clearFocus()
            if (!isAnimating) {
                isAnimating = true
                currentUserUid?.let { uid ->
                    toggleFollow(uid, friendId, isFriend) {

                    }
                }
            }
        },
        modifier = Modifier.size(45.dp)
    ) {
        when {
            isAnimating -> {  // Animation on click
                Image(
                    painter = rememberDrawablePainter(
                        drawable = getDrawable(
                            LocalContext.current,
                            if (isFriend) R.drawable.anim_beer_emptying else R.drawable.anim_beer_filling
                        )
                    ),
                    contentDescription = "Beer animation",
                    modifier = Modifier
                        .size(40.dp)
                        .fillMaxSize()
                )
                LaunchedEffect(Unit) {
                    delay(1000)
                    isAnimating = false
                    isFriend = !isFriend
                    val message = if (isFriend) {
                        "${context.getString(R.string.friend_added)} $friendName ${context.getString(R.string.emoji_beer)}"
                    } else {
                        "${context.getString(R.string.friend_deleted)} $friendName ${context.getString(R.string.emoji_cross)}"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
            else -> {  // After animation, icon
                Image(
                    painter = painterResource(id = if (isFriend) R.drawable.icon_beer_full else R.drawable.icon_beer_empty),
                    contentDescription = "Beer empty",
                    modifier = Modifier
                        .size(40.dp)
                        .fillMaxSize()
                )
            }
        }
    }
}
