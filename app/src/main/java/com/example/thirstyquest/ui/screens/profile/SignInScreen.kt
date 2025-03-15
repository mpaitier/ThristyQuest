package com.example.thirstyquest.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen

@Composable
fun SignInScreen(navController: NavController)
{
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() } // for auto focus
    val keyboardController = LocalSoftwareKeyboardController.current

    val isFormValid = emailOrUsername.isNotBlank() && password.isNotBlank()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Return")
            }
            Text(
                text = stringResource(R.string.sign_in),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        // login info
        OutlinedTextField(
            value = emailOrUsername,
            onValueChange = { emailOrUsername = it },
            label = { Text(stringResource(R.string.sign_in_id)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { /* TODO: Implement password reset */ }) {
            Text(stringResource(R.string.forgot_pwd))
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { if (isFormValid) navController.navigate(Screen.Profile.name) },
            enabled = isFormValid,
        ) {
            Text(stringResource(R.string.login))
        }
    }
}
