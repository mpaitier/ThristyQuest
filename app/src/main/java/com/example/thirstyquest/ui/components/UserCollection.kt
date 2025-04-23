package com.example.thirstyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.thirstyquest.R
import com.example.thirstyquest.data.Category
import com.example.thirstyquest.db.getUserCollection
import com.example.thirstyquest.ui.dialog.DrinkItem
import com.example.thirstyquest.ui.viewmodel.AuthViewModel

enum class SortCriterion(val label: String) {
    Name("Nom"),
    Level("Niveau")
}

@Composable
fun UserCollectionContent(authViewModel: AuthViewModel) {
    var fullList by remember { mutableStateOf<List<Category>>(listOf(Category(""))) }
    val uid = authViewModel.uid.observeAsState()
    val userId = uid.value ?: ""

    var selectedSort by remember { mutableStateOf(SortCriterion.Level) }
    var isAscending by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        fullList = getUserCollection(userId)
        fullList = sortCategories(fullList, selectedSort, isAscending)
    }

    fun onSortChanged(newCriterion: SortCriterion) {
        selectedSort = newCriterion
        fullList = sortCategories(fullList, selectedSort, isAscending)
    }
    fun onOrderChanged(newIsAscending: Boolean) {
        isAscending = newIsAscending
        fullList = sortCategories(fullList, selectedSort, isAscending)
    }

    if (fullList == listOf(Category(""))) {
        LoadingSection()
    } else if(fullList.isEmpty()) {
        Text(text = stringResource(R.string.hist_not_found), modifier = Modifier.padding(16.dp))
    }
    else {
        Column {
            SortBar(
                selectedSort = selectedSort,
                isAscending = isAscending,
                onSortChanged = ::onSortChanged,        // <-- call onSortChanged
                onOrderChanged = ::onOrderChanged       // <-- call onOrderChanged
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 60.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(fullList) { drink ->
                    DrinkItem(userId = userId, drink = drink)
                }
            }
        }
    }
}

fun sortCategories(list: List<Category>, criterion: SortCriterion, isAscending: Boolean): List<Category> {
    return when (criterion) {
        SortCriterion.Name -> {
            if (isAscending) list.sortedBy { it.name } else list.sortedByDescending { it.name }
        }
        SortCriterion.Level -> {
            if (isAscending)
                list.sortedWith(compareBy<Category> { it.level }.thenBy { it.points })
            else
                list.sortedWith(compareByDescending<Category> { it.level }.thenByDescending { it.points })
        }
    }
}

@Composable
fun SortBar(
    selectedSort: SortCriterion,
    isAscending: Boolean,
    onSortChanged: (SortCriterion) -> Unit,
    onOrderChanged: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .border(1.dp, MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = stringResource(R.string.tri), style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.width(8.dp))

        Box {
            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Text(
                    text = selectedSort.label,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                SortCriterion.entries.forEach { criterion ->
                    DropdownMenuItem(
                        text = { Text(criterion.label) },
                        onClick = {
                            onSortChanged(criterion)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onTertiaryContainer)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                val newIsAscending = !isAscending
                onOrderChanged(newIsAscending)
            }
        ) {
            Icon(
                imageVector = if (isAscending) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = if (isAscending) "Ordre croissant" else "Ordre décroissant"
            )
        }
    }
}
