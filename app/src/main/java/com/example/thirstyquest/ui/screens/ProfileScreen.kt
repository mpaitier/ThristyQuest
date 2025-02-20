package com.example.thirstyquest.ui.screens

import android.app.AlertDialog
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.*
import com.example.thirstyquest.R
import kotlinx.coroutines.launch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button


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
            text = stringResource(id = R.string.conso),
            fontSize = 20.sp,
            color = primaryColor,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("/Jour", "0.4")
            StatItem("/Mois", "15")
            StatItem("/Ans", "200")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.pref),
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
            text = stringResource(id = R.string.total),
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
fun Screen1() {
    ListBoisson()
}

@Composable
fun Screen2() {
    ListBadge()

}


@Composable
fun ListBoisson() {
    val boissonList = List(20) { Unit }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 colonnes
        contentPadding = PaddingValues(start = 20.dp, top = 15.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        items(boissonList) {
            ItemBoisson()
        }
    }
}


@Composable
fun ItemBoisson() {
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
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.yager),
                contentDescription = "Verre d'alcool",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Biere",
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
            title = { Text(text = "Gros Jäger", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.yager),
                        contentDescription = "Imageboisson",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Le goût incomparable du Jägermeister résulte d' un mélange parfait d'herbes, d'épices et de notes d'agrumes . Des composants d'agrumes acidulés comme l'écorce d'orange se marient à des herbes aromatiques comme le gingembre, l'anis étoilé et le clou de girofle, accompagnés d'une pointe de réglisse.", textAlign = TextAlign.Justify)
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Fermer")
                }
            }
        )
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
    var showDialog by remember { mutableStateOf(false) } // État pour afficher le dialog

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
            title = { Text(text = "Assiduité", fontWeight = FontWeight.Bold) },
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
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }

}
