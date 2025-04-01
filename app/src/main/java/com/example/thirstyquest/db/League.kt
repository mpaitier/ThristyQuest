package com.example.thirstyquest.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

fun addLeagueToFirestore(uid: String, name: String, onLeagueCreated: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val lid = db.collection("leagues").document().id

    val league = hashMapOf(
        "owner uid" to uid,
        "name" to name,
        "xp" to 0,
        "count" to 1
    )

    db.collection("leagues").document(lid)
        .set(league)
        .addOnSuccessListener {
            // Add user to members list
            db.collection("leagues").document(lid).collection("members").document(uid).set(hashMapOf(uid to uid))
            onLeagueCreated(lid)
            Log.d("FIRESTORE", "Ligue ajoutée !")
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur d'ajout : ", e)
        }
    db.collection("users").document(uid).collection("leagues").document(lid)
        .set(hashMapOf("League name" to name))
        .addOnSuccessListener { Log.d("FIRESTORE", "Ligue ajoutée à l'utilisateur !") }
        .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur lors de l'ajout à l'utilisateur : ", e) }
}

fun joinLeague(uid: String, lid: String, leagueName: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users").document(uid).collection("leagues").document(lid)
        .set(hashMapOf("League name" to leagueName))
        .addOnSuccessListener { Log.d("FIRESTORE", "Ligue rejointe !") }
        .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur lors de la jointure : ", e) }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//      Getters

suspend fun getAllUserLeaguesIdCoroutine(uid: String): List<String> {
    val db = FirebaseFirestore.getInstance()
    val leagueList = mutableListOf<String>()
    try {
        val result = db.collection("users").document(uid).collection("leagues")
            .get()
            .await()
        // Parcours tous les documents récupérés
        for (document in result) {
            // Récupère l'ID du document (c'est l'ID de l'ami)
            val id = document.id
            leagueList.add(id)
        }
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return leagueList
}

suspend fun getLeagueName(leagueID: String): String {
    val db = FirebaseFirestore.getInstance()
    var leagueName = ""
    try {
        val result = db.collection("leagues").document(leagueID).get().await()
        leagueName = result.get("name") as String
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return leagueName
}

suspend fun getLeagueOwnerId(leagueID: String): String {
    val db = FirebaseFirestore.getInstance()
    var ownerId = ""
    try {
        val result = db.collection("leagues").document(leagueID).get().await()
        ownerId = result.get("owner uid") as String
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return ownerId
}

suspend fun getLeagueMemberCount(leagueID: String): String {
    val db = FirebaseFirestore.getInstance()
    var nbrMembers = ""
    try {
        val result = db.collection("leagues").document(leagueID).get().await()
        nbrMembers = result.get("count").toString()
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return nbrMembers
}

suspend fun getUserRank(uid: String, leagueID: String): Int {
    val db = FirebaseFirestore.getInstance()

    return try {
        // Récupérer tous les UID des membres de la ligue
        val membersSnapshot = db.collection("leagues")
            .document(leagueID)
            .collection("members")
            .get()
            .await()

        val memberIds = membersSnapshot.documents.map { it.id }

        // Récupérer l'XP de chaque utilisateur
        val xpList = memberIds.mapNotNull { memberId ->
            val userSnapshot = db.collection("users").document(memberId).get().await()
            val xp = userSnapshot.getLong("xp") ?: return@mapNotNull null
            memberId to xp
        }

        // Trier par XP décroissant
        val rankedList = xpList.sortedByDescending { it.second }

        // Trouver la position de l'utilisateur
        val rank = rankedList.indexOfFirst { it.first == uid } + 1

        if (rank > 0) rank else 0
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur lors de la récupération du classement : ", e)
        -1
    }
}

suspend fun getAllLeagueMembers(leagueID: String): List<String> {
    val db = FirebaseFirestore.getInstance()
    return try {
        val membersSnapshot = db.collection("leagues")
            .document(leagueID)
            .collection("members")
            .get()
            .await()

        membersSnapshot.documents.map { it.id }
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur lors de la récupération des membres : ", e)
        emptyList()
    }
}


////////////////////////////////////////////////////////////////////////////////////////////////////
//      PUT

fun updateLeagueName(leagueID: String, newName: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("leagues").document(leagueID)
        .update("name", newName)
        .addOnSuccessListener {
            Log.d("FIRESTORE", "Nom mis à jour avec succès")
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la mise à jour du nom", e)
        }
}