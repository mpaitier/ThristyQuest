package com.example.thirstyquest.ui.screens

import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.*
import com.example.thirstyquest.R
import kotlinx.coroutines.launch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.example.thirstyquest.ui.components.Publication

data class Boisson(val name: String,
                   val imageRes: Int,
                   val description: String,
                   val level: Int,
                   val points: Int,
                   val nextLevelPoints: Int)

val hist = listOf(
    Publication(1, "Pinte de bête rouge au Bistrot", 26, "12/02/2025", "19:00","Biere" ),
    Publication(2, "Aguardiente chez Moe's", 12, "12/02/2025", "20:00","Liqueur"),
    Publication(3, "Jaggger chez Croguy", 84, "12/02/2025", "20:12","Jäger"),
    Publication(4, "Binch de malade", 2, "13/02/2025", "02:26","Biere"),
    Publication(5, "Ricard du midi", 1, "13/02/2025", "12:26","Ricard"),
    Publication(6, "Double IPA qui arrache", 4, "13/02/2025", "16:52","Biere"),
    Publication(7, "Bouteille de vin en mode classe", 18, "14/02/2025", "21:30", "Vin Rouge"),
    Publication(8, "Ricard pur x_x", 14, "15/02/2025", "19:15","Ricard"),
    Publication(9, "La potion de Shrek", 8, "15/02/2025", "01:26","Cimetière"),
    Publication(10, "Pinte à la Voie Maltée", 74, "16/02/2025", "10:28","Biere")
)


@Composable
fun ProfileScreen(navController: NavController) {
    val tabTitles = listOf(R.string.stats, R.string.collection,R.string.badge)
    val pagerState = rememberPagerState { tabTitles.size }
    val coroutineScope = rememberCoroutineScope()

   //val items = List(12) { it}

    LaunchedEffect(Unit) {
        pagerState.scrollToPage(1)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = { Text(stringResource(id = title)) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> Screen0()
                1 -> Screen1()
                2 -> Screen2()
            }
        }

    }

}

@Composable
fun Screen0() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.conso),
            fontSize = 20.sp,
            color = primaryColor,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("L/Jour", "0.4")
            StatItem("L/Mois", "15")
            StatItem("L/An", "200")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.pref),
            fontSize = 20.sp,
            color = primaryColor,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Biere blonde", "71")
            StatItem("Fire ball", "32")
        }
        Text(
            text = stringResource(R.string.total),
            fontSize = 20.sp,
            color = primaryColor,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Boissons consommées", "310")
        }
    }
}
@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}



@Composable
fun Screen2() {
    ListBadge()

}

@Composable
fun Screen1() {
    ListBoisson()
}
@Composable
fun ListBoisson() {
    val boissonList = listOf(
        Boisson("Biere", R.drawable.biere, "Une bière rafraîchissante.",10, 70, 80),
        Boisson("Vodka", R.drawable.vodka, "Une vodka premium.",3, 9, 10),
        Boisson("Coca", R.drawable.coca, "Boisson gazeuse classique.",1, 1, 3),
        Boisson("Jäger", R.drawable.yager, "Le goût incomparable du Jägermeister résulte d' un mélange parfait d'herbes, d'épices et de notes d'agrumes . Des composants d'agrumes acidulés comme l'écorce d'orange se marient à des herbes aromatiques comme le gingembre, l'anis étoilé et le clou de girofle, accompagnés d'une pointe de réglisse.",21, 103, 120),
        Boisson("Ricard",R.drawable.ricard,"Pastis à base d'anis, de réglisse et d'herbes de Provence.",3,7,10)
    )
    var sortedList by remember { mutableStateOf(boissonList) }
    var isSortedAsc by remember { mutableStateOf(true) }

    fun sortBoissons(ascending: Boolean) {
        sortedList = if (ascending) {
            boissonList.sortedBy { it.name }
        } else {
            boissonList.sortedByDescending { it.name }
        }
    }

    val secondaryColor = MaterialTheme.colorScheme.secondary

    // TODO : placer un ordre par consommation à la place de l'ordre alphabetique inverse lorsque la base de donnée sera là
    Column {

        Button(
            onClick = {
                isSortedAsc = !isSortedAsc
                sortBoissons(isSortedAsc)
            },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryColor,
            )
        ) {
            Text(text = if (isSortedAsc) "Trier par ordre décroissant" else "Trier par ordre croissant")
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // 3 colonnes
            contentPadding = PaddingValues(start = 20.dp, top = 15.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(sortedList) { boisson ->
                ItemBoisson(boisson = boisson)
            }
        }
    }
}


@Composable
fun ItemBoisson(boisson: Boisson) {
    var showDialog by remember { mutableStateOf(false) } // État pour afficher le dialog

    val primaryColor = MaterialTheme.colorScheme.primary

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
                painter = painterResource(id = boisson.imageRes),
                contentDescription = boisson.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = boisson.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(primaryColor, shape = CircleShape)
                            .clickable { showDialog = false },
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
                    Image(
                        painter = painterResource(id = boisson.imageRes),
                        contentDescription = boisson.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Nom : ${boisson.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Description : ${boisson.description}",
                        textAlign = TextAlign.Justify
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Niveau : ${boisson.level}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    LevelProgressBar(boisson.points, boisson.nextLevelPoints)

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredHist = hist.filter { it.category == boisson.name } // Filtrage
                    if (filteredHist.isNotEmpty()) {
                        Text(
                            text = "Historique :",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        filteredHist.forEachIndexed { index, publication ->
                            histItem(publication, index)
                        }
                    } else {
                        Text(text = "Aucun historique trouvé.", )
                    }
                }
            },
            confirmButton = {},
            containerColor = Color.White
        )
    }
}


@Composable
fun LevelProgressBar(currentXP: Int, maxXP: Int) {
    val progress = currentXP.toFloat() / maxXP.toFloat()
    val backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            color = tertiaryColor,
            trackColor = backgroundColor
        )

        Text(
            text = "$currentXP / $maxXP",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun histItem(publication: Publication, publicationNum: Int)
{
    // TODO : Add picture
    // TODO : Make picture clickable and navigate to publication details
    // TODO : Make user's name clickable and navigate to user's profile
    val name = "Membre n°${publication.user_ID}"                                                    // TODO : get member name with user_ID
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.AccountBox,
            contentDescription = "Publication picture",
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
                text = name,
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
                text = publication.heure,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
@Composable
fun ListBadge() {
    val boissonList = List(11) { Unit }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 colonnes
        contentPadding = PaddingValues(start = 20.dp, top = 15.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        items(boissonList) {
            ItemBadge()
        }
    }
}

@Composable
fun ItemBadge() {
    var showDialog by remember { mutableStateOf(false) }
    val primaryColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(100.dp)
            .clickable { showDialog = true },
        shape = CircleShape,

        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.badge),
            contentDescription = "badge",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Assiduité",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(primaryColor, shape = CircleShape)
                            .clickable { showDialog = false },
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
                    Image(
                        painter = painterResource(id = R.drawable.badge),
                        contentDescription = "badge",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Tu a bu les 7 jours de la semaine Bravo", textAlign = TextAlign.Justify)
                }
            },
            confirmButton = {}
        )
    }

}
