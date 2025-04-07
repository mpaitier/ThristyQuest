package com.example.thirstyquest.db

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.thirstyquest.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

////////////////////////////////////////////////////////////////////////////////////////////////////
//    POST

fun addLeagueToFirestore(uid: String, name: String, onLeagueCreated: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    fun generateLeagueId(): String {
        val digits = (0..9).toList()
        val letters = ('A'..'Z').toList()
        val random = java.util.Random()

        fun randomDigits(n: Int) = (1..n).map { digits[random.nextInt(digits.size)] }.joinToString("")
        fun randomLetter() = letters[random.nextInt(letters.size)]

        return randomDigits(3) + randomLetter() + randomDigits(3) + randomLetter() + randomDigits(3)
    }

    fun tryCreateLeague() {
        val lid = generateLeagueId()

        db.collection("leagues").document(lid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // L'ID est déjà utilisé, on en génère un autre
                    tryCreateLeague()
                } else {
                    // L'ID est libre, on peut créer la ligue
                    val league = hashMapOf(
                        "owner uid" to uid,
                        "name" to name,
                        "xp" to 0,
                        "count" to 1,
                        "total liters" to 0.0,
                        "total price" to 0.0,
                    )

                    db.collection("leagues").document(lid)
                        .set(league)
                        .addOnSuccessListener {
                            // Ajouter l'utilisateur à la ligue
                            db.collection("leagues").document(lid)
                                .collection("members").document(uid)
                                .set(hashMapOf(uid to uid))

                            // Lier la ligue à l'utilisateur
                            db.collection("users").document(uid)
                                .collection("leagues").document(lid)
                                .set(hashMapOf("League name" to name))

                            onLeagueCreated(lid)
                            Log.d("FIRESTORE", "Ligue ajoutée avec succès !")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FIRESTORE", "Erreur lors de la création de la ligue : ", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Erreur lors de la vérification de l'ID de ligue : ", e)
            }
    }

    tryCreateLeague()
}


fun joinLeagueIfExists(
    uid: String,
    leagueCode: String,
    context: Context,
    navController: NavController
) {
    val db = FirebaseFirestore.getInstance()

    db.collection("leagues").document(leagueCode).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                // Ajouter l'utilisateur comme membre
                db.collection("leagues").document(leagueCode)
                    .collection("members").document(uid)
                    .set(hashMapOf(uid to uid))

                // Lier la ligue à l'utilisateur
                val leagueName = document.getString("name") ?: "Ligue"
                db.collection("users").document(uid)
                    .collection("leagues").document(leagueCode)
                    .set(hashMapOf("League name" to leagueName))

                // Redirection
                navController.navigate(Screen.LeagueContent.name + "/$leagueCode")
            } else {
                Toast.makeText(context, "Code de ligue inconnu", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la vérification du code de ligue", e)
            Toast.makeText(context, "Erreur lors de la connexion", Toast.LENGTH_SHORT).show()
        }
}


////////////////////////////////////////////////////////////////////////////////////////////////////
//    GET

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

suspend fun getLeagueXp(leagueID: String): Double {
    val db = FirebaseFirestore.getInstance()
    var xp = 0.0
    try {
        val result = db.collection("leagues").document(leagueID).get().await()
        xp = result.get("xp") as Double
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return xp
}

suspend fun getLeagueTotalLiters(leagueID: String): Double {
    val db = FirebaseFirestore.getInstance()
    var totalLiters = 0.0
    try {
        val result = db.collection("leagues").document(leagueID).get().await()
        totalLiters = result.get("total liters") as Double
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return totalLiters
}

suspend fun getLeagueTotalPrice(leagueID: String): Double {
    val db = FirebaseFirestore.getInstance()
    var totalPrice = 0.0
    try {
        val result = db.collection("leagues").document(leagueID).get().await()
        totalPrice = result.get("total price") as Double
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return totalPrice
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