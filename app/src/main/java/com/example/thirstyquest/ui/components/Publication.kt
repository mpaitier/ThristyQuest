package com.example.thirstyquest.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.data.Publication
import com.example.thirstyquest.navigation.Screen
import coil.compose.AsyncImage
import com.example.thirstyquest.R
import com.example.thirstyquest.db.getUserLastPublications
import com.example.thirstyquest.db.getUserNameCoroutine
import com.example.thirstyquest.ui.dialog.PublicationDetailDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel

@Composable
fun PublicationItemLeague(publication: Publication, publicationNum: Int, navController: NavController)
{
    var userName by remember { mutableStateOf("") }

    LaunchedEffect(publication.user_ID) {
        userName = getUserNameCoroutine(publication.user_ID)
    }

    val interactionSource = remember { MutableInteractionSource() }
    var showPublicationDialog by remember { mutableStateOf(false) }

    val drawableMap = mapOf( // TODO : SUPPRIMER QUAND ON UTILISERA PLUS LES PUBLI EN DUR CODES POUR LE TEST
        "drawable_biere" to com.example.thirstyquest.R.drawable.biere,
        "drawable_ricard" to com.example.thirstyquest.R.drawable.ricard,
        "drawable_vodka" to com.example.thirstyquest.R.drawable.vodka
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (publication.photo.startsWith("http")) {
            AsyncImage(
                model = publication.photo,
                contentDescription = "Publication picture",
                modifier = Modifier
                    .size(80.dp)
                    .clickable { showPublicationDialog = true },
                contentScale = ContentScale.Crop
            )
        } else {
            val drawableRes = drawableMap[publication.photo] ?: com.example.thirstyquest.R.drawable.ricard
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = "Publication picture",
                modifier = Modifier
                    .size(80.dp)
                    .clickable { showPublicationDialog = true },
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = publication.description,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                color = if (publicationNum % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(interactionSource = interactionSource, indication = null) {
                    navController.navigate(Screen.FriendProfile.name + "/${publication.user_ID}")
                },
            )
        }

        Column {
            Text(
                text = publication.date,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.End)
            )
            Text(
                text = publication.hour,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showPublicationDialog) {
        PublicationDetailDialog(publication = publication, onDismiss = { showPublicationDialog= false })
    }
}




@Composable
fun UserPublications(authViewModel: AuthViewModel) {
    val uid = authViewModel.uid.observeAsState()
    val userId = uid.value ?: ""
    var publications by remember { mutableStateOf<List<Publication>>(emptyList()) }
    LaunchedEffect(userId) {
        getUserLastPublications(userId) { newList ->
            publications = newList
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(top = 15.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        items(publications) { publication ->
            PublicationItem(publication)
        }
    }
}

@Composable
fun PublicationItem(publication: Publication) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .clickable { showDialog = true }
    ) {
        if (!publication.photo.isNullOrEmpty() && publication.photo.startsWith("http")) {
            AsyncImage(
                model = publication.photo,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.biere),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (showDialog) {
        PublicationDetailDialog(publication = publication) {
            showDialog = false
        }
    }
}


@Composable
fun FriendPublications(friendId: String) {
    var publications by remember { mutableStateOf<List<Publication>>(emptyList()) }
    var showMorePublications by remember { mutableStateOf(false) }
    var selectedPublication by remember { mutableStateOf<Publication?>(null) }

    LaunchedEffect(friendId) {
        getUserLastPublications(friendId) { newList ->
            publications = newList
        }
    }

    val displayedPublications = if (showMorePublications) publications else publications.take(3)

    Column(modifier = Modifier.fillMaxWidth()) {
        FriendPublicationItem(
            items = displayedPublications,
            columns = 3,
            onItemClick = { publication -> selectedPublication = publication }
        )

        if (publications.size > 3) {
            Button(
                onClick = { showMorePublications = !showMorePublications },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            ) {
                Text(if (showMorePublications) "-" else "+")
            }
        }

        selectedPublication?.let {
            PublicationDetailDialog(publication = it) {
                selectedPublication = null
            }
        }
    }
}


@Composable
fun FriendPublicationItem(
    items: List<Publication>,
    columns: Int,
    modifier: Modifier = Modifier,
    onItemClick: (Publication) -> Unit
) {
    Column(modifier = modifier) {
        items.chunked(columns).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                            .clickable { onItemClick(item) }
                    ) {
                        if (!item.photo.isNullOrEmpty() && item.photo.startsWith("http")) {
                            AsyncImage(
                                model = item.photo,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.biere),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}