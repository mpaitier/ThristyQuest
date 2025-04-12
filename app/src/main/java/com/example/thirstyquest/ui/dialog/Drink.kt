package com.example.thirstyquest.ui.dialog

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.thirstyquest.R
import com.example.thirstyquest.data.Category
import com.example.thirstyquest.data.Publication
import com.example.thirstyquest.ui.components.DrinkProgressBar
import com.example.thirstyquest.data.Drink

val drawableMap = mapOf(
    "drawable_biere" to R.drawable.biere,
    "drawable_ricard" to R.drawable.ricard,
    "drawable_vodka" to R.drawable.vodka
)

@Composable
fun DrinkDetailDialog (
    onDismiss: () -> Unit,
    drink: Category,
    hist: List<Publication>,
    icon: Painter
)
{
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Fermer",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }
        },
        text = {
            Column {
                // Drink's picture
                Image(
                    painter = icon,
                    contentDescription = drink.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Drink's name
                Text(
                    text = stringResource(R.string.name) + ": ${drink.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Drink's description
                /*Text(
                    text = stringResource(R.string.description) + ": ${drink.description}",
                    textAlign = TextAlign.Justify
                )*/
                Spacer(modifier = Modifier.height(8.dp))
                // Drink's level
                Text(
                    text = stringResource(R.string.level) + ": ${drink.level}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                //DrinkProgressBar(drink.points, drink.nextLevelPoints,modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(12.dp))
                val filteredHist = hist.filter { it.category == drink.name }
                if (filteredHist.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.hist) + ":",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(
                        modifier = Modifier.height(300.dp)
                    ) {
                        // Utilisation de itemsIndexed pour afficher les éléments
                        itemsIndexed(filteredHist) { index, publication ->
                            DrinkHistItem(publication, index)
                        }
                    }


                } else {
                    Text(text = stringResource(R.string.hist_not_found))
                }
            }
        },
        confirmButton = {},
        containerColor = Color.White
    )
}

@Composable
fun DrinkHistItem(publication: Publication, publicationNum: Int)
{
    // TODO : Make user's name clickable and navigate to user's profile
    var showDialog = remember { mutableStateOf(false) }
    val price = "${publication.price} €"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                showDialog.value = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = publication.photo,
            contentDescription = "Publication picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column (modifier = Modifier.weight(1f)) {
            Text(
                publication.description,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                color = if (publicationNum % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

                )
            Text(
                text = price,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    if (showDialog.value) {
        PublicationDetailDialog(onDismiss = { showDialog.value = false }, publication = publication)
    }
}

@Composable
fun PublicationHistItem(publication: Publication)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (publication.photo.startsWith("http")) {
            AsyncImage(
                model = publication.photo,
                contentDescription = "Image de la boisson",
                modifier = Modifier.size(40.dp)
            )
        } else {
            val drawableRes = drawableMap[publication.photo] ?: R.drawable.ricard
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = "Image par défaut",
                modifier = Modifier.size(40.dp)
            )
        }


        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(publication.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            Text("Points: ${publication.points}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

// Fonction pour mapper chaque boisson à son icône
@Composable
fun getDrinkIcon(name: String): Int {
    return when (name) {
        "Bière" -> R.drawable.biere
        "Vin" -> R.drawable.vin
        "Cocktail" -> R.drawable.cocktail
        "Shot" -> R.drawable.shot
        else -> R.drawable.other
    }
}

@Composable
fun AllDrinksDialog(
    drinks: List<Pair<String, Int>>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Points des boissons",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column {
                Text(
                    text = "Tu peux ici consulter les points attribués à chaque boisson. Ces points changent toutes les 3 heures, sont attribués aléatoirement et déterminent le score que tu gagnes à chaque consommation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = "Distribution actuelle :",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                drinks.forEach { (name, points) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = getDrinkIcon(name)),
                            contentDescription = name,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$points pts",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    )
}


