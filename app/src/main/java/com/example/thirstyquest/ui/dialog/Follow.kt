package com.example.thirstyquest.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thirstyquest.R
import com.example.thirstyquest.data.User
import com.example.thirstyquest.ui.screens.social.FollowItem
import com.example.thirstyquest.data.followersList
import com.example.thirstyquest.data.followingList
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.compose.runtime.LaunchedEffect


@Composable
fun FollowDialog(userID: String, onDismiss: () -> Unit, navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    // États pour stocker les données Firestore
    var followersList by remember { mutableStateOf<List<User>>(emptyList()) }
    var followingList by remember { mutableStateOf<List<User>>(emptyList()) }
    var showFollowers by remember { mutableStateOf(true) }

    // 🔥 Récupérer les abonnés en temps réel
    LaunchedEffect(userID) {
        db.collection("users").document(userID).collection("followers")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FIRESTORE", "Erreur de chargement des followers", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val newList = mutableListOf<User>()
                    snapshot.documents.forEach { doc ->
                        val followerId = doc.id
                        db.collection("users").document(followerId).get()
                            .addOnSuccessListener { userDoc ->
                                val user = userDoc.toObject(User::class.java)
                                user?.let { newList.add(it) }
                            }
                    }
                    followersList = newList
                }
            }
    }

// 🔥 Récupérer les suivis en temps réel
    LaunchedEffect(userID) {
        db.collection("users").document(userID).collection("following")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FIRESTORE", "Erreur de chargement des following", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val newList = mutableListOf<User>()
                    snapshot.documents.forEach { doc ->
                        val followingId = doc.id
                        db.collection("users").document(followingId).get()
                            .addOnSuccessListener { userDoc ->
                                val user = userDoc.toObject(User::class.java)
                                user?.let { newList.add(it) }
                            }
                    }
                    followingList = newList
                }
            }
    }


    // Interface utilisateur
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contacts", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Choix entre abonnés et suivis
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showFollowers = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showFollowers) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Abonnés")
                    }
                    Button(
                        onClick = { showFollowers = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showFollowers) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Suivis")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Liste des abonnés ou des suivis
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(325.dp)
                ) {
                    if (showFollowers) {
                        FollowersPage(followersList, navController)
                    } else {
                        FollowingPage(followingList, navController)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}


@Composable
fun FollowersPage(followersList: List<User>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(followersList.size) { index ->
            FollowItem(followersList[index], navController)
        }
    }
}

@Composable
fun FollowingPage(followingList: List<User>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(followingList.size) { index ->
            FollowItem(followingList[index], navController)
        }
    }
}

@Composable
fun FriendsPage(friendsList: List<User>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(friendsList.size) { index ->
            FollowItem(friendsList[index], navController)
        }
    }
}

fun addUserToFirestore(userId: String, name: String, level: Int) {
    val db = FirebaseFirestore.getInstance()
    val user = User(userId, name, level, false)  // Pas encore d'amis

    db.collection("users").document(userId)
        .set(user)
        .addOnSuccessListener { Log.d("FIRESTORE", "Utilisateur ajouté !") }
        .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur d'ajout : ", e) }
}
fun getUsersFromFirestore() {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject(User::class.java)
                Log.d("FIRESTORE", "Utilisateur récupéré : $user")
            }
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
        }
}
fun updateUserLevel(userId: String, newLevel: Int) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .document(userId)
        .update("level", newLevel)
        .addOnSuccessListener {
            Log.d("FIRESTORE", "Niveau mis à jour avec succès !")
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la mise à jour du niveau : ", e)
        }
}
fun deleteUserFromFirestore(userId: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .document(userId)
        .delete()
        .addOnSuccessListener {
            Log.d("FIRESTORE", "Utilisateur supprimé avec succès !")
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la suppression de l'utilisateur : ", e)
        }
}
fun sendFriendRequest(fromUserId: String, toUserId: String) {
    val db = FirebaseFirestore.getInstance()

    val request = hashMapOf("from" to fromUserId, "status" to "pending")

    db.collection("users").document(toUserId)
        .collection("friend_requests")
        .document(fromUserId)
        .set(request)
        .addOnSuccessListener { Log.d("FIRESTORE", "Demande envoyée !") }
        .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur : ", e) }
}
fun acceptFriendRequest(currentUserId: String, friendUserId: String) {
    val db = FirebaseFirestore.getInstance()

    val friendship = hashMapOf("since" to System.currentTimeMillis())

    db.collection("users").document(currentUserId)
        .collection("friends").document(friendUserId)
        .get()
        .addOnSuccessListener { document ->
            if (!document.exists()) {
                db.collection("users").document(currentUserId)
                    .collection("friends").document(friendUserId)
                    .set(friendship)

                db.collection("users").document(friendUserId)
                    .collection("friends").document(currentUserId)
                    .set(friendship)

                db.collection("users").document(currentUserId)
                    .collection("friend_requests").document(friendUserId)
                    .delete()
                    .addOnSuccessListener { Log.d("FIRESTORE", "Ami ajouté !") }
                    .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur : ", e) }
            } else {
                Log.d("FIRESTORE", "Cette amitié existe déjà.")
            }
        }
}

fun getFriends(userId: String, onResult: (List<User>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users").document(userId)
        .collection("friends")
        .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FIRESTORE", "Erreur : ", e)
                return@addSnapshotListener
            }

            val friends = mutableListOf<User>()
            snapshot?.documents?.forEach { doc ->
                val friendId = doc.id

                db.collection("users").document(friendId).get()
                    .addOnSuccessListener { friendDoc ->
                        val friend = friendDoc.toObject(User::class.java)
                        friend?.let { friends.add(it) }
                        onResult(friends)  // Mettre à jour la liste
                    }
            }
        }
}
