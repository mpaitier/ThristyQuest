package com.example.thirstyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.thirstyquest.data.Category
import com.example.thirstyquest.db.getCollectionUser
import com.example.thirstyquest.ui.dialog.DrinkItem
import com.example.thirstyquest.ui.viewmodel.AuthViewModel


@Composable
fun UserCollectionContent(authViewModel: AuthViewModel) {
    var fullList by remember { mutableStateOf<List<Category>>(emptyList()) }
    val uid = authViewModel.uid.observeAsState()
    val userId = uid.value ?: ""
    var isAscending by remember { mutableStateOf(true) }
    var selectedSort by remember { mutableStateOf("Niveau") }

    LaunchedEffect(userId) {
        fullList = getCollectionUser(userId)
    }

    fun sortDrinks(type: String) {
        if (selectedSort == type) {
            isAscending = !isAscending
        } else {
            selectedSort = type
            isAscending = true
        }

        fullList = when (selectedSort) {
            "Nom" -> if (isAscending) fullList.sortedBy { it.name } else fullList.sortedByDescending { it.name }
            "Niveau" -> if (isAscending) fullList.sortedBy { it.level } else fullList.sortedByDescending { it.level }
            else -> fullList
        }
    }


    if( fullList.isEmpty() ) {
        LoadingSection()
    }
    else {
        Column {
            SortButton(selectedSort, isAscending) { sortDrinks(it) }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 15.dp,
                    end = 20.dp,
                    bottom = 60.dp
                ),
                modifier = Modifier.fillMaxHeight()
            ) {

                items(fullList) { drink ->
                    DrinkItem(userId = userId, drink = drink)
                }
            }
        }
    }
}


@Composable
fun SortButton(selectedSort: String, isAscending: Boolean, onSortSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Trier par : $selectedSort (${if (isAscending) "⬆️" else "⬇️"})")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
        ) {
            DropdownMenuItem(
                text = { Text("Nom") },
                onClick = {
                    onSortSelected("Nom")
                    expanded = false
                },
                colors = MenuDefaults.itemColors(textColor = Color.White)
            )
            DropdownMenuItem(
                text = { Text("Level") },
                onClick = {
                    onSortSelected("Level")
                    expanded = false
                },
                colors = MenuDefaults.itemColors(textColor = Color.White)
            )
        }
    }
}
