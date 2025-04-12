package com.example.thirstyquest.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thirstyquest.R
import com.example.thirstyquest.data.Category
import com.example.thirstyquest.data.Drink
import com.example.thirstyquest.data.Publication
import com.example.thirstyquest.ui.dialog.DrinkDetailDialog
import com.example.thirstyquest.data.PublicationHist
import com.example.thirstyquest.db.getCollectionUser
import com.example.thirstyquest.db.getUserLastPublications
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


    Column {
        SortButton(selectedSort, isAscending) { sortDrinks(it) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 60.dp),
            modifier = Modifier.fillMaxHeight()
        ) {

            items(fullList) { drink ->
                val icon = when (drink.name) {
                    "Bière" -> painterResource(id = R.drawable.biere)
                    "Vin" -> painterResource(id = R.drawable.vin)
                    "Cocktail" -> painterResource(id = R.drawable.cocktail)
                    "Shot" -> painterResource(id = R.drawable.shot)
                    else -> painterResource(id = R.drawable.other)
                }
                DrinkItem(authViewModel = authViewModel, drink = drink, icon = icon)
            }
        }
    }
}


@Composable
fun DrinkItem(authViewModel: AuthViewModel, drink: Category, icon: Painter) {
    var showDialog by remember { mutableStateOf(false) }
    var publications by remember { mutableStateOf<List<Publication>>(emptyList()) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val userId by authViewModel.uid.observeAsState()

    LaunchedEffect(userId) {
        userId?.let { uid ->
            getUserLastPublications(uid) { newList -> publications = newList }
        }
    }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { showDialog = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(100.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(Color(0xFFFFFFFF))
        ) {
            Image(
                painter = icon,
                contentDescription = drink.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = drink.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showDialog) {
        DrinkDetailDialog(onDismiss = {showDialog=false}, drink = drink, hist = publications, icon = icon)
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
