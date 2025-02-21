package com.example.thirstyquest.ui.screens.social

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.example.thirstyquest.R
import com.example.thirstyquest.navigation.Screen
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

import com.example.thirstyquest.ui.components.LeagueHistScreenContent
import com.example.thirstyquest.ui.components.LeagueMembersScreenContent
import com.example.thirstyquest.ui.components.LeagueStatsScreenContent

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LeagueContentScreen(leagueID: Int, navController: NavController) {
    val pagerState = rememberPagerState(initialPage = 1)
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current  // for share intent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LeagueTopBar(navController, leagueID)
        Spacer(modifier = Modifier.height(16.dp))
        LeagueInfo(
            leagueID,
            onShareClick = { shareMessage ->
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                    type = "text/plain"
                }

                val chooserIntent = Intent.createChooser(shareIntent, "Partager via")
                context.startActivity(chooserIntent)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            count = 3, // Trois pages : Stats, Détails, Historique
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> LeagueStatsScreenContent(leagueID) // Page gauche
                1 -> LeagueMembersScreenContent(navController, leagueID) // Page centrale
                2 -> LeagueHistScreenContent(leagueID) // Page droite
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BottomDots(pagerState)
    }
}

//////////////////////////////////////////////////////////////////////////////////
//                                Composables

// ------------------------------ League Top Bar ------------------------------
@Composable
fun LeagueTopBar(navController: NavController, leagueID: Int) {
    var showDialog by remember { mutableStateOf(false) }

    val leagueName = "Ligue $leagueID"          // TODO : get league name with leagueID
    val leagueOwnerId = 2                       // TODO : get league's owner's ID  with leagueID
    val ownID = leagueID                        // TODO : get own ID

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height((0.04166*LocalConfiguration.current.screenHeightDp).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(imageVector = Icons.Filled.Menu,       // TODO : replace by league picture
            contentDescription = "League picture",
            modifier = Modifier.size(60.dp))

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = leagueName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        // TODO : navigate to league settings & visible only if user is owner
        if(ownID == leagueOwnerId) {
            IconButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Modifier")
            }
        }
    }

    if (showDialog) {
        ModifyLeagueDialog(
            onDismiss = { showDialog = false },
            onValidate = { leagueName ->
                showDialog = false
                // TODO : modify league's name
                // TODO : modify league's picture
            },
            leagueID = leagueID
        )
    }
}

// ------------------------------ League Info ------------------------------
@Composable
fun LeagueInfo(leagueID: Int, onShareClick: (String) -> Unit) {
    // TODO : get informations with leagueID
    val leagueCode =  "ABCD"  // Vous allez récupérer ce code avec leagueID
    val currentLevel = 3
    val nextLevel = 4
    val currentXP = 900
    val requiredXP = 1000

    Column(
        modifier = Modifier.height(100.dp)
    ) {
        // League XP progress
        Text(
            text = stringResource(id = R.string.league_level),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))
        LeagueXPProgress(currentLevel, nextLevel, currentXP, requiredXP)
        Spacer(modifier = Modifier.height(18.dp))

        // League code & share button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Code de ligue : ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append(leagueCode)
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(40.dp))
            // Share button
            IconButton(
                onClick = {
                    val leagueName = "Scrott League"
                    val shareMessage =
                        "Viens rejoindre la ligue $leagueName sur Thirsty Quest et partageons nos aventures de consommation ! \uD83C\uDF7B\n" +
                                "Voici mon code de ligue : $leagueCode\n"
                    onShareClick(shareMessage)
                },
                modifier = Modifier.size(16.dp)
            ) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
            }
        }
    }
}

// ------------------------------ League XP Progress ------------------------------
@Composable
fun LeagueXPProgress(currentLevel: Int, nextLevel: Int, currentXP: Int, requiredXP: Int) {
    val progress = currentXP.toFloat() / requiredXP

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Niv. $currentLevel",
                style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Niv. $nextLevel", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "$currentXP / $requiredXP XP",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// ------------------------------ Bottom Dots ------------------------------
@Composable
fun BottomDots(pagerState: PagerState) {
    val pageCount = 3
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            val color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primaryContainer
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

//
@Composable
fun ModifyLeagueDialog(
    onDismiss: () -> Unit,
    onValidate: (String) -> Unit,
    leagueID: Int
) {
    var leagueName by remember { mutableStateOf("Ligue $leagueID") }          // TODO : get league name with leagueID
    // TODO : get league picture with leagueID

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(140.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {

                    }

                    // Add picture button
                    IconButton(
                        onClick = { /* TODO: Add picture selection */ },
                        modifier = Modifier
                            .size(48.dp)
                            .offset(x = 8.dp, y = 8.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircleOutline,
                            contentDescription = "Add picture",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // League's name entry
                OutlinedTextField(
                    value = leagueName,
                    onValueChange = { newLeagueName -> leagueName = newLeagueName },
                    label = { Text(stringResource(R.string.league_add_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(24.dp))

                // Cancel & validate buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel button
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    // Validate button
                    Button(
                        onClick = { if (leagueName.isNotBlank()) onValidate(leagueName) },  // TODO : Add picture save
                        enabled = leagueName.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.validate))
                    }
                }
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////
//                               Previews

@Preview(showBackground = true)
@Composable
fun PreviewLeagueTopBar() {
    LeagueTopBar(navController = rememberNavController(), leagueID = 12)
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueInfo() {
    LeagueInfo(leagueID = 12, onShareClick = { /* Share league code */ })
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueXPProgress() {
    LeagueXPProgress(currentLevel = 3, nextLevel = 4, currentXP = 600, requiredXP = 1000)
}
