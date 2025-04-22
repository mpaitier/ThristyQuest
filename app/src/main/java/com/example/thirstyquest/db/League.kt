package com.example.thirstyquest.db

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import co.yml.charts.common.model.Point
import com.example.thirstyquest.navigation.Screen
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale

////////////////////////////////////////////////////////////////////////////////////////////////////
//    POST

fun addLeagueToFirestore(
    context: Context,
    uid: String,
    name: String,
    imageUri: Uri?,
    onLeagueCreated: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    fun generateLeagueId(): String {
        val digits = (0..9).toList()
        val letters = ('A'..'Z').toList()
        val random = java.util.Random()

        fun randomDigits(n: Int) = (1..n).map { digits[random.nextInt(digits.size)] }.joinToString("")
        fun randomLetter() = letters[random.nextInt(letters.size)]

        return randomDigits(2) + randomLetter() + randomLetter() + randomDigits(2)
    }

    fun createLeagueWithPhotoUrl(lid: String, imageUrl: String?) {
        val league = hashMapOf(
            "owner uid" to uid,
            "name" to name,
            "xp" to 0,
            "count" to 1,
            "total liters" to 0.0,
            "total price" to 0.0
        ).apply {
            if (!imageUrl.isNullOrEmpty()) this["photoUrl"] = imageUrl
        }

        db.collection("leagues").document(lid)
            .set(league)
            .addOnSuccessListener {
                db.collection("leagues").document(lid)
                    .collection("members").document(uid)
                    .set(hashMapOf(uid to uid))

                db.collection("users").document(uid)
                    .collection("leagues").document(lid)
                    .set(hashMapOf("League name" to name))

                onLeagueCreated(lid)
            }
    }

    fun tryCreateLeague() {
        val lid = generateLeagueId()

        db.collection("leagues").document(lid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    tryCreateLeague()
                } else {
                    // 👇 Upload photo si dispo
                    if (imageUri != null) {
                        uploadLeagueImageToFirebase(lid, imageUri, context) { imageUrl ->
                            createLeagueWithPhotoUrl(lid, imageUrl)
                        }
                    } else {
                        createLeagueWithPhotoUrl(lid, null)
                    }
                }
            }
    }

    tryCreateLeague()
}

fun uploadLeagueImageToFirebase(
    id: String,
    uri: Uri,
    context: Context,
    onUploaded: (String?) -> Unit
) {
    val storageRef = FirebaseStorage.getInstance().reference.child("leagueImages/$id.jpg")

    try {
        val originalBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val fixedBitmap = rotateBitmapIfRequired(context, uri, originalBitmap)

        val baos = ByteArrayOutputStream()
        fixedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos) // Compression à 60%
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                onUploaded(downloadUri)
            } else {
                onUploaded(null)
            }
        }
    } catch (e: Exception) {
        Log.e("UPLOAD", "Erreur upload image de ligue", e)
        onUploaded(null)
    }
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
                db.collection("leagues").document(leagueCode)
                    .collection("members").document(uid)
                    .get()
                    .addOnSuccessListener { member ->
                        if (member.exists()) {
                            Toast.makeText(context, "Vous êtes déjà membre de cette ligue", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            // Ajouter l'utilisateur comme membre
                            db.collection("leagues").document(leagueCode)
                                .collection("members").document(uid)
                                .set(hashMapOf(uid to uid))

                            // Lier la ligue à l'utilisateur
                            val leagueName = document.getString("name") ?: "Ligue"
                            db.collection("users").document(uid)
                                .collection("leagues").document(leagueCode)
                                .set(hashMapOf("League name" to leagueName))

                            db.collection("leagues").document(leagueCode)
                                .update("count", FieldValue.increment(1))
                        }
                        // Redirection
                        navController.navigate(Screen.LeagueContent.name + "/$leagueCode")
                    }
            }
            else {
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

suspend fun getLeagueTotalPublications(leagueID: String): Int {
    val db = FirebaseFirestore.getInstance()
    return try {
        val result = db.collection("leagues")
            .document(leagueID)
            .collection("publications")
            .get()
            .await()

        result.size()
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des publications de ligue : ", e)
        0
    }
}

suspend fun getLeagueUserStats(leagueID: String): List<Pair<String, Double>> {
    val db = FirebaseFirestore.getInstance()

    var maxDrink = Pair("", Double.MIN_VALUE)
    var minDrink = Pair("", Double.MAX_VALUE)
    var maxPaid = Pair("", Double.MIN_VALUE)
    var minPaid = Pair("", Double.MAX_VALUE)

    try {
        val membersSnapshot = db.collection("leagues")
            .document(leagueID)
            .collection("members")
            .get()
            .await()

        for (doc in membersSnapshot.documents) {
            val userID = doc.id

            try {
                val userSnapshot = db.collection("users")
                    .document(userID)
                    .get()
                    .await()

                val name = userSnapshot.getString("name") ?: continue
                val drink = userSnapshot.getDouble("total drink") ?: 0.0
                val paid = userSnapshot.getDouble("total paid") ?: 0.0

                if (drink > maxDrink.second) maxDrink = name to drink
                if (drink < minDrink.second) minDrink = name to drink

                if (paid > maxPaid.second) maxPaid = name to paid
                if (paid < minPaid.second) minPaid = name to paid

            } catch (e: Exception) {
                Log.w("FIRESTORE", "Erreur récupération stats utilisateur $userID", e)
            }
        }

    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur récupération des membres de la ligue", e)
    }

    return listOf(maxDrink, minDrink, maxPaid, minPaid)
}

suspend fun getTop3Categories(leagueID: String): List<Pair<String, Long>> {
    val db = FirebaseFirestore.getInstance()
    val categoryRef = db.collection("leagues").document(leagueID).collection("category")

    return try {
        val snapshot = categoryRef.get().await()

        val categoryList = snapshot.documents.mapNotNull { doc ->
            val total = doc.getLong("total")
            val name = doc.id
            if (total != null) name to total else null
        }.sortedByDescending { it.second }

        val paddedList = categoryList.take(3).toMutableList()

        while (paddedList.size < 3) {
            paddedList.add("" to -1L)
        }

        paddedList
    } catch (e: Exception) {
        Log.e("Firebase", "Erreur lors de la récupération des catégories", e)
        listOf("" to -1L, "" to -1L, "" to -1L)
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

fun updateLeaguePhotoUrl(leagueId: String, photoUrl: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("leagues").document(leagueId)
        .update("photoUrl", photoUrl)
        .addOnSuccessListener {
            Log.d("Firebase", "Photo de ligue mise à jour avec succès")
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Erreur mise à jour photo ligue", e)
        }
}
