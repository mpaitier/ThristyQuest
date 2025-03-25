package com.example.thirstyquest.db

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thirstyquest.data.User
import com.example.thirstyquest.ui.dialog.FollowersPage
import com.example.thirstyquest.ui.dialog.FollowingPage
import com.example.thirstyquest.ui.screens.social.FollowItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


/*
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
*/

// Ajouter un utilisateur à Firestore
/*
fun addUserToFirestore(uid: String, name: String) {
    val db = FirebaseFirestore.getInstance()
    val user = hashMapOf(
        "uid" to uid,
        "name" to name
    )

    // Ajouter l'utilisateur
    db.collection("users").document(uid)
        .set(user)
        .addOnSuccessListener {
            createEmptyCollections(db, uid)
            Log.d("FIRESTORE", "Utilisateur ajouté !")
        }
        .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur d'ajout : ", e) }
}
*/

private fun createEmptyCollections(db: FirebaseFirestore, uid: String) {
    val collections = listOf("following","followers")

    for (collection in collections) {
        db.collection("users").document(uid).collection(collection)
            .add(hashMapOf("initialized" to true))
            .addOnSuccessListener { Log.d("FIRESTORE", "Collection $collection créée !") }
            .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur sur $collection : ", e) }
    }
}

// Récupérer le nom d'un utilisateur
fun getUserName(uid: String): String {
    val db = FirebaseFirestore.getInstance()
    var name = ""

    db.collection("users").document(uid)
        .get()
        .addOnSuccessListener { document ->
            name = document.getString("name") ?: ""
        }

    return name
}
suspend fun getUserNameCoroutine(uid: String): String {
    val db = FirebaseFirestore.getInstance()
    return try {
        val document = db.collection("users").document(uid).get().await()
        document.getString("name") ?: ""
    } catch (e: Exception) {
        ""
    }
}
/*
// Récupérer tous les utilisateurs
suspend fun getAllUsers(): List<Pair<String, String>> {
    val db = FirebaseFirestore.getInstance()
    val userList = mutableListOf<Pair<String, String>>()

    try {
        val result = db.collection("users").get().await()
        for (document in result) {
            val id = document.getString("uid") ?: ""
            val name = document.getString("name") ?: ""
            userList.add(id to name)
        }
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return userList
}
*/
// Récupérer tous les abonnés d'un utilisateur
fun getAllFollowerId(uid: String): List<String> {
    val db = FirebaseFirestore.getInstance()
    var followerList = mutableListOf<String>()
    db.collection("users").document(uid).collection("followers")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                followerList.add(document.getString("uid") ?: "")
            }
        }
    return followerList
}
fun getAllFollowingId(uid: String): List<String> {
    val db = FirebaseFirestore.getInstance()
    var followingList = mutableListOf<String>()
    db.collection("users").document(uid).collection("following")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                followingList.add(document.getString("uid") ?: "")
            }
        }
    return followingList
}
suspend fun getAllFollowingIdCoroutine(uid: String): List<String> {
    val db = FirebaseFirestore.getInstance()
    val friendList = mutableListOf<String>()
    try {
        val result = db.collection("users").document(uid).collection("following")
            .get()
            .await()
        // Parcours tous les documents récupérés
        for (document in result) {
            // Récupère l'ID du document (c'est l'ID de l'ami)
            val id = document.id
            friendList.add(id)
        }
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return friendList
}


fun getFollowerStatus(uid:String,friendId: String):Boolean{
    val db = FirebaseFirestore.getInstance()
    var isFriend = false
    db.collection("users").document(uid).collection("followers").document(friendId)
        .get()
        .addOnSuccessListener { document ->
            isFriend = document.exists()
        }
    return isFriend

}
fun setFollowerStatus(uid: String, friendId: String, isFriend: Boolean) {
    val db = FirebaseFirestore.getInstance()
    if(isFriend){
        db.collection("users").document(uid).collection("followers").document(friendId)
            .set(hashMapOf("isFriend" to isFriend))
            .addOnSuccessListener { Log.d("FIRESTORE", "Ami ajouté !") }
            .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur", e) }
    }

}
fun getFollowingStatus(uid:String,friendId: String):Boolean{
    val db = FirebaseFirestore.getInstance()
    var isFriend = false
    db.collection("users").document(uid).collection("following").document(friendId)
        .get()
        .addOnSuccessListener { document ->
            isFriend = document.exists()
        }
    return isFriend

}
fun setFollowingStatus(uid: String, friendId: String, isFriend: Boolean) {
    val db = FirebaseFirestore.getInstance()
    if(isFriend){
        db.collection("users").document(uid).collection("following").document(friendId)
            .set(hashMapOf("isFriend" to isFriend))
            .addOnSuccessListener { Log.d("FIRESTORE", "Ami ajouté !") }
            .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur", e) }
    }

}



//-----------------------------------------------------------------------------------------------------------------------------
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
