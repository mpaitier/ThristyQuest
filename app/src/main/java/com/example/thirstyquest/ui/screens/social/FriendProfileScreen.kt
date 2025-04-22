package com.example.thirstyquest.ui.screens.social

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import co.yml.charts.common.model.Point
import coil.compose.AsyncImage
import com.example.thirstyquest.R
import com.example.thirstyquest.data.Category
import com.example.thirstyquest.db.calculateLevelAndRequiredXP
import com.example.thirstyquest.db.getUserCollection
import com.example.thirstyquest.db.getFollowerCount
import com.example.thirstyquest.db.getFollowingCount
import com.example.thirstyquest.db.getMonthConsumptionPoints
import com.example.thirstyquest.db.getMonthVolumeConsumptionPoints
import com.example.thirstyquest.db.getTop2CategoriesByTotal
import com.example.thirstyquest.db.getTotalDrinkVolume
import com.example.thirstyquest.db.getTotalMoneySpent
import com.example.thirstyquest.db.getUserLastPublications
import com.example.thirstyquest.db.getUserNameById
import com.example.thirstyquest.db.getUserXPById
import com.example.thirstyquest.db.getWeekConsumptionPoints
import com.example.thirstyquest.db.getWeekVolumeConsumptionPoints
import com.example.thirstyquest.db.getYearConsumptionPoints
import com.example.thirstyquest.db.getYearVolumeConsumptionPoints
import com.example.thirstyquest.navigation.Screen
import com.example.thirstyquest.ui.components.AddFriendButton
import com.example.thirstyquest.ui.components.ConsumptionChart
import com.example.thirstyquest.ui.components.FriendPublications
import com.example.thirstyquest.ui.components.LoadingSection
import com.example.thirstyquest.ui.components.ProgressBar
import com.example.thirstyquest.ui.components.StatItemColumn
import com.example.thirstyquest.ui.dialog.DrinkItem
import com.example.thirstyquest.ui.dialog.FollowDialog
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@SuppressLint("DefaultLocale")
@Composable
fun FriendProfileScreen(friendId: String, navController: NavController, authViewModel: AuthViewModel) {
    var showFriendsListDialog by remember { mutableStateOf(false) }
    var showPublicationsDialog by remember { mutableStateOf(false) }
    var showMoreCollection by remember { mutableStateOf(false) }
    var defaultTabFollowers by remember { mutableStateOf(true) }

    var friendName by remember { mutableStateOf<String?>(null) }

    var publicationNumber by remember { mutableIntStateOf(0) }
    var followerNumber by remember { mutableIntStateOf(0) }
    var followingNumber by remember { mutableIntStateOf(0) }
    var photoUrl by remember { mutableStateOf<String?>("") }
    var fullList by remember { mutableStateOf<List<Category>>(emptyList()) }
    val visibleItems = if (showMoreCollection) fullList else fullList.take(3)

    // Statistics
    var totalVolume by remember { mutableDoubleStateOf(0.0) }
    var totalMoneySpent by remember { mutableDoubleStateOf(0.0) }

    var totalDrink1 by remember { mutableStateOf<Category?>(null) }
    var totalDrink2 by remember { mutableStateOf<Category?>(null) }

    var weeklyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var monthlyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var yearlyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }

    var weeklyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var monthlyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var yearlyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var showedList by remember { mutableStateOf<List<Point>>(listOf(Point(0f, 0f))) }

    LaunchedEffect(friendId) {
        getUserLastPublications(friendId) { newList ->
            publicationNumber = newList.size
        }
        followerNumber = getFollowerCount(friendId)
        followingNumber = getFollowingCount(friendId)

        getUserNameById(friendId) { name ->
            friendName = name
        }

        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .document(friendId)
            .get()
            .await()
        photoUrl = snapshot.getString("photoUrl")

        fullList = getUserCollection(friendId)

        totalVolume = getTotalDrinkVolume(friendId)
        totalMoneySpent = getTotalMoneySpent(friendId)

        val topCategories = getTop2CategoriesByTotal(friendId)
        totalDrink1 = topCategories.getOrNull(0)
        totalDrink2 = topCategories.getOrNull(1)

        weeklyConsumptionList = getWeekConsumptionPoints(friendId, "users")
        monthlyConsumptionList = getMonthConsumptionPoints(friendId, "users")
        yearlyConsumptionList = getYearConsumptionPoints(friendId, "users")

        weeklyVolumeList = getWeekVolumeConsumptionPoints(friendId, "users")
        monthlyVolumeList = getMonthVolumeConsumptionPoints(friendId, "users")
        yearlyVolumeList = getYearVolumeConsumptionPoints(friendId, "users")
        showedList = weeklyConsumptionList
    }

    Column {
        // ========================= TOP BAR =========================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour"
                )
            }
            Text(
                text = friendName ?: "Nom non trouvé",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        // ========================= CONTENT =========================
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {

            item {
                // ------------------------- HEADER -------------------------
                FriendProfileHeader(
                    friendId,
                    photoUrl.toString(),
                    publicationNumber,
                    followerNumber,
                    followingNumber,
                    onPublicationClick = { showPublicationsDialog = true },
                    onFollowerClick = { showFriendsListDialog = true
                        defaultTabFollowers = true },
                    onFollowingClick = { showFriendsListDialog = true
                        defaultTabFollowers = false},
                    authViewModel
                )
                Spacer(modifier = Modifier.height(16.dp))
                // ------------------------- PUBLICATIONS -------------------------
                Text(
                    text = "Publications récentes",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                FriendPublications(friendId)

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(
                        modifier = Modifier.width(200.dp),
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 1.dp
                    )
                }

                // ------------------------- COLLECTION -------------------------
                Text(
                    text = "Collection",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                if (fullList.isEmpty()) {
                    Text(
                        text = "Aucune collection trouvée",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 0.dp, max = 500.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        items(visibleItems) { drink ->
                            DrinkItem(userId = friendId, drink = drink)
                        }
                    }
                    if (fullList.size > 3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = { showMoreCollection = !showMoreCollection },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onBackground
                                )
                            ) {
                                Text(if (showMoreCollection) "-" else "+")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(
                        modifier = Modifier.width(200.dp),
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 1.dp
                    )
                }
                // ------------------------- STATS -------------------------
                Text(
                    text = "Statistiques",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(horizontal = 10.dp))
                {
                    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                    // Total part
                    Text(
                        text = stringResource(R.string.total),
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItemColumn(stringResource(R.string.consumed_drink), String.format("%.2f", totalVolume))
                        StatItemColumn("€ "+stringResource(R.string.spent_money), String.format("%.2f", totalMoneySpent))

                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                    // Preferences part
                    Text(
                        text = stringResource(R.string.pref),
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if(totalDrink1 == null && totalDrink2 == null) {
                        Text(
                            text = "Aucune publication trouvée",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            totalDrink1?.let {
                                StatItemColumn(it.name, it.total.toString())
                            }

                            totalDrink2?.let {
                                StatItemColumn(it.name, it.total.toString())
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                    // Consummation part
                    Text(
                        text = stringResource(R.string.conso),
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Duration & volume selection
                    var selectedDuration by remember { mutableStateOf("Dans la semaine") }
                    var durationExpanded by remember { mutableStateOf(false) }
                    val durationSelection =
                        listOf("Dans la semaine", "Dans le mois", "Dans l'année")

                    var selectedVolume by remember { mutableStateOf("Verres consommés") }
                    var volumeExpanded by remember { mutableStateOf(false) }
                    val volumeSelection = listOf("Verres consommés", "Litres consommés")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            TextButton(onClick = { volumeExpanded = true }) {
                                Text(selectedVolume, color = MaterialTheme.colorScheme.tertiary)
                                Icon(
                                    imageVector = if (volumeExpanded) Icons.AutoMirrored.Filled.ArrowBack else Icons.Filled.ArrowDownward,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            DropdownMenu(
                                expanded = volumeExpanded,
                                onDismissRequest = { volumeExpanded = false }
                            ) {
                                volumeSelection.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = {
                                            selectedVolume = unit
                                            when (unit) {
                                                "Verres consommés" ->
                                                    when (selectedDuration) {
                                                        "Dans la semaine" -> showedList =
                                                            weeklyConsumptionList

                                                        "Dans le mois" -> showedList =
                                                            monthlyConsumptionList

                                                        "Dans l'année" -> showedList =
                                                            yearlyConsumptionList
                                                    }

                                                "Litres consommés" ->
                                                    when (selectedDuration) {
                                                        "Dans la semaine" -> showedList =
                                                            weeklyVolumeList

                                                        "Dans le mois" -> showedList =
                                                            monthlyVolumeList

                                                        "Dans l'année" -> showedList =
                                                            yearlyVolumeList
                                                    }
                                            }
                                            volumeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1F))
                        Box {
                            TextButton(onClick = { durationExpanded = true }) {
                                Text(selectedDuration, color = MaterialTheme.colorScheme.tertiary)
                                Icon(
                                    imageVector = if (durationExpanded) Icons.AutoMirrored.Filled.ArrowBack else Icons.Filled.ArrowDownward,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            DropdownMenu(
                                expanded = durationExpanded,
                                onDismissRequest = { durationExpanded = false }
                            ) {
                                durationSelection.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = {
                                            selectedDuration = unit
                                            when (unit) {
                                                "Dans la semaine" -> when (selectedVolume) {
                                                    "Verres consommés" -> showedList =
                                                        weeklyConsumptionList

                                                    "Litres consommés" -> showedList =
                                                        weeklyVolumeList
                                                }

                                                "Dans le mois" -> when (selectedVolume) {
                                                    "Verres consommés" -> showedList =
                                                        monthlyConsumptionList

                                                    "Litres consommés" -> showedList =
                                                        monthlyVolumeList
                                                }

                                                "Dans l'année" -> when (selectedVolume) {
                                                    "Verres consommés" -> showedList =
                                                        yearlyConsumptionList

                                                    "Litres consommés" -> showedList =
                                                        yearlyVolumeList
                                                }
                                            }
                                            durationExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (
                        weeklyConsumptionList == listOf(Point(-1f, -1f)) ||
                        monthlyConsumptionList == listOf(Point(-1f, -1f)) ||
                        yearlyConsumptionList == listOf(Point(-1f, -1f)) ||
                        weeklyVolumeList == listOf(Point(-1f, -1f)) ||
                        monthlyVolumeList == listOf(Point(-1f, -1f)) ||
                        yearlyVolumeList == listOf(Point(-1f, -1f))
                    ) {
                        LoadingSection()
                    } else {
                        ConsumptionChart(showedList, selectedDuration)
                    }
                }
            }
        }

        if (showFriendsListDialog) {
            FollowDialog(
                uid = friendId,
                onDismiss = { showFriendsListDialog = false },
                navController = navController,
                authViewModel = authViewModel,
                defaultTabFollowers = defaultTabFollowers
            )
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//    Composable

@Composable
fun FriendProfileHeader(friendId: String, photoUrl:String, publicationNumber: Int, followerNumber : Int, followingNumber: Int, onPublicationClick: () -> Unit , onFollowerClick: () -> Unit, onFollowingClick: () -> Unit, authViewModel: AuthViewModel)
{
    var showImageFullscreen by remember { mutableStateOf(false) }
    var userXP by remember { mutableDoubleStateOf(0.0) }
    var currentLevel by remember { mutableIntStateOf(1) }
    var requiredXP by remember { mutableIntStateOf(2000) }

    LaunchedEffect(friendId) {
        getUserXPById(friendId) { xp ->
            userXP = xp ?: 0.0
            val (lvl, reqXP) = calculateLevelAndRequiredXP(xp?: 0.0)
            currentLevel = lvl
            requiredXP = reqXP
        }
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            ) {
                if (photoUrl != "null") {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Photo de profil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { showImageFullscreen = true },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.pdp),
                        contentDescription = "Profil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoFriendStatItem(
                    title = stringResource(R.string.publi),
                    count = publicationNumber,
                    onClick = onPublicationClick
                )
                InfoFriendStatItem(
                    title = stringResource(R.string.followers),
                    count = followerNumber,
                    onClick = onFollowerClick
                )
                InfoFriendStatItem(
                    title = stringResource(R.string.following),
                    count = followingNumber,
                    onClick = onFollowingClick
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ProgressBar(
                currentLevel = currentLevel,
                currentXP = (userXP % requiredXP).toInt(),
                requiredXP = requiredXP,
                modifier = Modifier
                    .weight(0.5F)
                    .height(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
            AddFriendButton(friendId = friendId, authViewModel = authViewModel)
        }
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    if (showImageFullscreen) {
        Dialog(onDismissRequest = { showImageFullscreen = false }) {
            Box(
                modifier = Modifier
                    .height(screenHeight/2)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "League image fullscreen",
                    modifier = Modifier
                        .padding(32.dp)
                        .height((screenHeight/2)-20.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun InfoFriendStatItem(title: String, count: Int, onClick: () -> Unit)
{
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "$count", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun FollowItem(uid:String, navController: NavController, authViewModel: AuthViewModel)
{
    var userName by remember { mutableStateOf<String?>(null) }

    // Fetch the user name based on the uid
    LaunchedEffect(uid) {
        getUserNameById(uid) { name ->
            userName = name
        }
    }

    fun navigateToProfile() {
        val destination = if (uid == authViewModel.uid.toString()) {
            Screen.Profile.name
        } else {
            Screen.FriendProfile.name + "/${uid}"
        }
        navController.navigate(destination)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Profile picture
        Image(
            painter = painterResource(id = R.drawable.pdp),
            contentDescription = "Profile",
            modifier = Modifier
                .size(50.dp)
                .clickable { navigateToProfile() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Username
        Text(
            text = userName?:"",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.clickable { navigateToProfile() }
        )
        Spacer(modifier = Modifier.weight(1F))
        // Friend button
        AddFriendButton(uid, authViewModel)
    }
}


