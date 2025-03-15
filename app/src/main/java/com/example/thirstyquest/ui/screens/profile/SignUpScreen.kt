package com.example.thirstyquest.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.AddPicture

@Composable
fun SignUpScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isBasicInfoFilled = email.isNotBlank() && username.isNotBlank() && password.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Return")
            }
            Text(
                text = stringResource(R.string.sign_up),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        // email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // user name
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.pseudo)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Profile picture
        AddPicture()
        Spacer(modifier = Modifier.height(8.dp))
        // Validation
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { if (isBasicInfoFilled) navController.navigate(Screen.Profile.name) },
            enabled = isBasicInfoFilled
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Text(stringResource(R.string.sign_up))
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}
