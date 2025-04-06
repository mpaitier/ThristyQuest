package com.example.thirstyquest.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thirstyquest.data.Publication
import com.example.thirstyquest.navigation.Screen
import coil.compose.AsyncImage
import com.example.thirstyquest.ui.dialog.PublicationDetailDialog

@Composable
fun PublicationItemLeague(publication: Publication, publicationNum: Int, navController: NavController)
{
    val name = "Membre n°${publication.user_ID}"
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
                text = name,
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