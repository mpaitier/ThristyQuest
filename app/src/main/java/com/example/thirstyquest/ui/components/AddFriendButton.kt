package com.example.thirstyquest.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.thirstyquest.db.getUserName
import com.example.thirstyquest.db.getFollowerStatus
import com.example.thirstyquest.db.setFollowingStatus
import com.example.thirstyquest.db.setFollowerStatus
import com.example.thirstyquest.db.getFollowingStatus
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.delay

@Composable
fun AddFriendButton(friendId :String, authViewModel: AuthViewModel) {   // TODO : la biere est affichée en fonction de si la personne est présente ou non dans following
    val userName = getUserName(friendId)
    var isFriend by remember { mutableStateOf(getFollowingStatus(authViewModel._uid, friendId)) }
    var isAnimating by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current


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
            isAnimating && !isFriend -> {
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
                    setFollowingStatus(authViewModel._uid, friendId, true)
                    setFollowerStatus(friendId, authViewModel._uid, true)
                    isFriend = true
                    Toast.makeText(
                        context,
                        "$userName ${context.getString(R.string.friend_added)}",
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
                    setFollowingStatus(authViewModel._uid, friendId, false)
                    setFollowerStatus(friendId, authViewModel._uid, false)
                    isFriend = false
                    Toast.makeText(
                        context,
                        "$userName ${context.getString(R.string.friend_deleted)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            isFriend -> {
                Image(
                    painter = painterResource(id = R.drawable.icon_beer_full),
                    contentDescription = "Beer full",
                    modifier = Modifier
                        .size(40.dp)
                        .fillMaxSize()
                )
            }

            !isFriend -> {
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