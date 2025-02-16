package com.example.thirstyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.example.thirstyquest.R
import com.example.thirstyquest.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    val isDarkTheme = isSystemInDarkTheme()

    val primaryColor = if (isDarkTheme) md_theme_dark_primary else md_theme_light_primary
    val secondaryColor = if (isDarkTheme) md_theme_dark_secondary else md_theme_light_secondary
    val tertiaryColor = if (isDarkTheme) md_theme_dark_tertiary else md_theme_light_tertiary
    val outlineColor = if (isDarkTheme) md_theme_dark_outline else md_theme_light_outline
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
            containerColor = Color.Transparent
        )
    )
}
