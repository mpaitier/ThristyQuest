package com.example.thirstyquest.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.example.thirstyquest.R
import com.example.thirstyquest.data.User
import com.example.thirstyquest.data.userList
import com.example.thirstyquest.ui.screens.social.AddFriendButton

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
fun SearchResultsList(query: String) {

    // Filter user's research
    val filteredUsers = userList.filter { it.name.contains(query, ignoreCase = true) }

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
        filteredUsers.forEach { user ->
            SearchResultsItem(user = user, query = query)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// --------------------------------- Search results ---------------------------------
@Composable
fun SearchResultsItem(user: User, query: String) {
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
            val startIndex = user.name.indexOf(query, ignoreCase = true)
            if (startIndex != -1) {
                append(user.name.substring(0, startIndex))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(user.name.substring(startIndex, startIndex + query.length))  // query is bold
                }
                append(user.name.substring(startIndex + query.length))
            } else {
                append(user.name)
            }
        }
        Text(
            text = annotatedName,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1F))
        AddFriendButton(isFriend = user.isFriend, userName = user.name)

    }
}