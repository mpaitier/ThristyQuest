package com.example.thirstyquest.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

////////////////////////////////////////////////////////////////////////////////////////////////////
//      POST

fun addUserToFirestore(uid: String, name: String, profileImageUrl: String? = null) {
    val db = FirebaseFirestore.getInstance()
    val user = hashMapOf(
        "uid" to uid,
        "name" to name,
        "xp" to 0,
        "photoUrl" to profileImageUrl,
        "total drink" to 0.0,
        "total paid" to 0.0,
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
//      GET

suspend fun doesUsernameExist(name: String): Boolean {
    val db = FirebaseFirestore.getInstance()
    val trimmedName = name.trim()

    return try {
        val querySnapshot = db.collection("users")
            .whereEqualTo("name", trimmedName)
            .get()
            .await()

        !querySnapshot.isEmpty
    } catch (e: Exception) {
        Log.e("Firestore", "Erreur lors de la vérification du pseudo", e)
        false
    }
}

suspend fun getUserNameCoroutine(uid: String): String {
    val db = FirebaseFirestore.getInstance()
    return try {
        val document = db.collection("users").document(uid).get().await()
        document.getString("name") ?: ""
    }
    catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur lors de la récupération du nom d'utilisateur", e)
        ""
    }
}

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


////////////////////////////////////////////////////////////////////////////////////////////////////
// XP to level conversion

fun calculateLevelAndRequiredXP(xp: Double, baseXp: Int = 1000  , growthRate: Double = 1.12): Pair<Int, Int> {
    var level = 1
    var totalXpForNextLevel = baseXp.toDouble()
    var actualXP = xp

    while (actualXP >= totalXpForNextLevel) {
        level++
        actualXP -= totalXpForNextLevel
        totalXpForNextLevel = (totalXpForNextLevel * growthRate).toDouble()
    }

    return level to totalXpForNextLevel.toInt()
}