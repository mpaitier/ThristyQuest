package com.example.thirstyquest.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

////////////////////////////////////////////////////////////////////////////////////////////////////
//      Setters

fun addUserToFirestore(uid: String, name: String, profileImageUrl: String? = null) {
    val db = FirebaseFirestore.getInstance()
    val user = hashMapOf(
        "uid" to uid,
        "name" to name,
        "xp" to 0,
        "photoUrl" to profileImageUrl
    )


    db.collection("users").document(uid)
        .set(user)
        .addOnSuccessListener {
            createEmptyCollections(db, uid)
            Log.d("FIRESTORE", "Utilisateur ajouté avec photo !")
        }
        .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur d'ajout : ", e) }
}




private fun createEmptyCollections(db: FirebaseFirestore, uid: String) {
    val collections = listOf("following","followers")

    for (collection in collections) {
        db.collection("users").document(uid).collection(collection)
            .add(hashMapOf("initialized" to true))
            .addOnSuccessListener { Log.d("FIRESTORE", "Collection $collection créée !") }
            .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur sur $collection : ", e) }
    }
}

fun updateUserName(userId: String, newName: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users").document(userId)
        .update("name", newName)
        .addOnSuccessListener {
            Log.d("FIRESTORE", "Nom mis à jour avec succès")
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la mise à jour du nom", e)
        }
}

fun updateUserProfilePhotoUrl(userId: String, photoUrl: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(userId)
        .update("photoUrl", photoUrl)
        .addOnSuccessListener {
            Log.d("Firebase", "Photo de profil mise à jour avec succès")
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Erreur mise à jour photo profil", e)
        }
}


////////////////////////////////////////////////////////////////////////////////////////////////////
//      Getters

fun getUserNameById(uid: String, onResult: (String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val userName = document.getString("name")
                onResult(userName)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la récupération de l'utilisateur", e)
            onResult(null)
        }
}

fun getUserXPById(uid: String, onResult: (Double?) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val xp = document.getDouble("xp")
                onResult(xp)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la récupération de l'utilisateur", e)
            onResult(null)
        }
}

fun getUserProfileImageUrl(uid: String, onResult: (String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(uid)
        .get()
        .addOnSuccessListener { document ->
            val url = document.getString("profileImageUrl")
            onResult(url)
        }
        .addOnFailureListener {
            onResult(null)
        }
}


fun getAllUsersExcept(uidToExclude: String, onResult: (List<Map<String, Any>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .whereNotEqualTo("uid", uidToExclude) // Exclude specified user
        .get()
        .addOnSuccessListener { snapshot ->
            val users = snapshot.documents.mapNotNull { it.data }
            onResult(users)
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la récupération des utilisateurs", e)
            onResult(emptyList())
        }
}

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

fun getAllUserLeague(uid: String, onResult: (List<String>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val leagueList = mutableListOf<String>()

    db.collection("users").document(uid).collection("leagues")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                leagueList.add(document.id)
            }
            onResult(leagueList)
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur de récupération des ligues : ", e)
            onResult(emptyList())
        }
}

fun getLastPublications(uid: String) : List<String> {
    val db = FirebaseFirestore.getInstance()
    var publicationList = mutableListOf<String>()

    db.collection("users").document(uid).collection("publications")
        .orderBy("date", Query.Direction.DESCENDING)
        .orderBy("hour", Query.Direction.DESCENDING)
        .limit(10)
        .get()
        .addOnSuccessListener { result ->
            publicationList = result.map { it.id } as MutableList<String>
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur de récupération des publications : ", e)
        }

    return publicationList
}

////////////////////////////////////////////////////////////////////////////////////////////////////
// XP to level conversion

fun getUserLevelFromXP(xp: Double): Int {
    var level = 1
    var xpNeeded = 100.0  // XP nécessaire pour passer au niveau suivant
    var totalXP = 0.0  // XP cumulé jusqu'à présent

    while (xp >= totalXP + xpNeeded) {
        totalXP += xpNeeded
        level++

        // Augmente l'XP nécessaire jusqu'à 1000 (pour le niveau 20)
        if (level <= 20) {
            xpNeeded += 50.0
            if (xpNeeded > 1000) xpNeeded = 1000.0
        } else {
            xpNeeded = 1000.0  // À partir du niveau 21, il faut toujours 1000 XP
        }
    }

    return level
}

