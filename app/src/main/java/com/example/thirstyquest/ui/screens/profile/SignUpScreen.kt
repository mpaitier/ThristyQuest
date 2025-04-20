package com.example.thirstyquest.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.viewmodel.AuthState
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import com.example.thirstyquest.db.updateUserProfilePhotoUrl
import com.example.thirstyquest.db.uploadImageToFirebase
import com.example.thirstyquest.ui.dialog.AddProfilePictureDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel)
{
    var pseudo by remember {mutableStateOf("")}
    var email by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showPhotoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> {
                showPhotoDialog = true
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }


    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Return")
            }
            Text(
                text = stringResource(R.string.sign_up),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Pseudo section
        OutlinedTextField(
            value = pseudo,
            onValueChange = {
                pseudo = it
            },
            label = {
                Text(stringResource(R.string.pseudo))
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Email section
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(stringResource(R.string.email))
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Password section
        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(stringResource(R.string.password))
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                // Icon to hide or not password
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Masquer le mot de passe" else "Afficher le mot de passe"
                    )
                }
            },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (authState.value != AuthState.Loading) {
                authViewModel.signup(email = email, password = password, pseudo = pseudo)
            }

        },

            enabled = authState.value != AuthState.Loading
        ) {
            Text(stringResource(R.string.sign_up))
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
            navController.navigate(Screen.SignIn.name)
        }) {
            Text(stringResource(R.string.already_account))
        }
    }

    if (showPhotoDialog) {
        AddProfilePictureDialog(
            onDismiss = {
                showPhotoDialog = false
                navController.navigate(Screen.MainMenu.name)
            },
            onImageCaptured = { uri ->
                scope.launch {
                    val userId = authViewModel.uid.value ?: return@launch
                    val url = uploadImageToFirebase(userId, uri, context)
                    if (url != null) {
                        updateUserProfilePhotoUrl(userId, url)
                    }
                    navController.navigate(Screen.MainMenu.name)
                }
            }

        )
    }
}
