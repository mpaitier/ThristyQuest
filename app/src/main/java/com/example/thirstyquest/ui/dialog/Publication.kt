package com.example.thirstyquest.ui.dialog

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.thirstyquest.R
import com.example.thirstyquest.data.DrinkCategories
import com.example.thirstyquest.data.Publication
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.thirstyquest.db.addPublicationToFirestore
import com.example.thirstyquest.db.addPublicationToLeague
import com.example.thirstyquest.db.getAllUserLeague
import com.example.thirstyquest.db.uploadImageToFirebase
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@Composable
fun PublicationDetailDialog(publication: Publication, onDismiss: () -> Unit)
{
    val dateTimeString = "${publication.date} ${publication.hour}"
    val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
    val parsedDate = inputFormat.parse(dateTimeString) ?: ""

    val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.FRENCH)
    val formattedDate = outputFormat.format(parsedDate)
    var showImageFullscreen by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = publication.description,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { showImageFullscreen = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (publication.photo.startsWith("http")) {
                        AsyncImage(
                            model = publication.photo,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ricard),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Points: ${publication.points}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Date: $formattedDate", fontSize = 16.sp)
                Text("Prix: ${publication.price} €", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.close), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    if (showImageFullscreen) {
        Dialog(onDismissRequest = { showImageFullscreen = false }) {
            Box{
                AsyncImage(
                    model = publication.photo,
                    contentDescription = "League image fullscreen",
                    modifier = Modifier
                        .padding(24.dp)
                        .height((screenHeight/2)-20.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun AddPublicationDialog(
    userId: String,
    onDismiss: () -> Unit,
    imageUri: Uri?,
    onSuccess: () -> Unit
) {
    var drinkName by remember { mutableStateOf("") }
    var drinkPrice by remember { mutableStateOf("") }
    var drinkCategory by remember { mutableStateOf("") }
    var drinkVolume by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val volumeOptions = listOf(
        "Shot (4cl)" to 4,
        "Demi/Verre (25cl)" to 25,
        "Bouteille (33cl)" to 33,
        "Pinte (50cl)" to 50,
        "Pichet (1L)" to 100
    )

    var showNameError by remember { mutableStateOf(false) }
    var showCategoryError by remember { mutableStateOf(false) }
    var showVolumeError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    showNameError = drinkName.isBlank()
                    showCategoryError = drinkCategory.isBlank()
                    showVolumeError = (drinkVolume == 0)

                    if (showNameError || showCategoryError || showVolumeError) return@Button

                    coroutineScope.launch {
                        isLoading = true

                        val url = imageUri?.let { uploadImageToFirebase(userId, it, context) } ?: ""


                        val publicationInfo = addPublicationToFirestore(
                            userId, drinkName, drinkPrice, drinkCategory, drinkVolume, url
                        )

                        getAllUserLeague(uid = userId) { leagueList ->
                            leagueList.forEach { leagueId ->
                                addPublicationToLeague(
                                    pid = publicationInfo.first,
                                    lid = leagueId,
                                    price = drinkPrice.toDoubleOrNull() ?: 0.0,
                                    volume = drinkVolume.toDouble(),
                                    points = publicationInfo.second,
                                    category = drinkCategory
                                )
                            }
                        }

                        isLoading = false
                        onDismiss()
                        onSuccess()
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(stringResource(R.string.add))
                }
            }
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)

        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                imageUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = stringResource(R.string.captured_picture),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                // ---------------- Category ----------------
                CategoryAutoComplete(
                    selectedCategory = drinkCategory,
                    onCategorySelected = {
                        drinkCategory = it
                        showCategoryError = false
                    },
                    showError = showCategoryError
                )
                Spacer(modifier = Modifier.height(16.dp))
                // ---------------- Name ----------------
                NameInput(
                    name = drinkName,
                    onNameChange = {
                        drinkName = it
                        showNameError = false
                    },
                    showError = showNameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                // ---------------- Price  ----------------
                PriceInput(
                    price = drinkPrice,
                    onPriceChange = { drinkPrice = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                // ---------------- Volume selector ----------------
                DropdownSelector(
                    selectedLabel = volumeOptions.find { it.second == drinkVolume }?.first,
                    options = volumeOptions,
                    onOptionSelected = {
                        drinkVolume = it
                        showVolumeError = false
                    },
                    showError = showVolumeError,
                    placeholder = stringResource(R.string.choose_volume)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

@Composable
fun CategoryAutoComplete(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    showError: Boolean
) {
    val allCategories = DrinkCategories.basePoints.keys.toList()
    var textFieldValue by remember { mutableStateOf(TextFieldValue(selectedCategory)) }
    var filteredSuggestions by remember { mutableStateOf(allCategories) }
    var showSuggestions by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Input field
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    showSuggestions = newValue.text.isNotEmpty()
                    filteredSuggestions = allCategories.filter {
                        it.contains(newValue.text, ignoreCase = true)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.choose_category),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }

        // Suggestions list
        if (showSuggestions && filteredSuggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                items(filteredSuggestions.size) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCategorySelected(filteredSuggestions[index])
                                textFieldValue = TextFieldValue(
                                    filteredSuggestions[index],
                                    TextRange(filteredSuggestions[index].length)
                                )
                                showSuggestions = false
                            }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = filteredSuggestions[index],
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        } else if (showSuggestions && filteredSuggestions.isEmpty()) {
            Text(
                text = stringResource(R.string.no_suggestion_found),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 12.dp, top = 8.dp)
            )
        }

        // Error message
        if (showError) {
            Text(
                stringResource(R.string.league_image),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun NameInput(
    name: String,
    onNameChange: (String) -> Unit,
    showError: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (name.isEmpty()) {
                    Text(
                        text = stringResource(R.string.drink_name),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
                BasicTextField(
                    value = name,
                    onValueChange = {
                        onNameChange(it)
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }
        }
        if (showError) {
            Text(
                text = stringResource(R.string.name_required),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun PriceInput(
    price: String,
    onPriceChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            if (price.isEmpty()) {
                Text(
                    text = stringResource(R.string.cost),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }
            BasicTextField(
                value = price,
                onValueChange = onPriceChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun DropdownSelector(
    selectedLabel: String?,
    options: List<Pair<String, Int>>,
    onOptionSelected: (Int) -> Unit,
    showError: Boolean,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clickable { expanded = true },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedLabel ?: placeholder,
                    color = if (selectedLabel == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { (label, value) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onOptionSelected(value)
                        expanded = false
                    }
                )
            }
        }

        if (showError) {
            Text(
                text = stringResource(R.string.volume_required),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}
