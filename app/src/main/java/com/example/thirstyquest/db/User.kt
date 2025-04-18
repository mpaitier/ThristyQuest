package com.example.thirstyquest.db

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.concurrent.CountDownLatch

////////////////////////////////////////////////////////////////////////////////////////////////////
//      Setters

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
//      Getters

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

@RequiresApi(Build.VERSION_CODES.O)
fun getUserConsumptionRawStats(
    userId: String,
    onResult: (List<Number>) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("publications")
        .whereEqualTo("user_ID", userId)
        .get()
        .addOnSuccessListener { documents ->

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val now = LocalDate.now()

            var volumeDay = 0.0
            var volumeWeek = 0.0
            var volumeMonth = 0.0
            var volumeYear = 0.0

            var countDay = 0
            var countWeek = 0
            var countMonth = 0
            var countYear = 0

            for (doc in documents) {
                val dateStr = doc.getString("date") ?: continue
                val volume = doc.getDouble("volume") ?: 0.0
                val date = try {
                    LocalDate.parse(dateStr, formatter)
                } catch (e: Exception) {
                    continue
                }

                val daysBetween = ChronoUnit.DAYS.between(date, now)
                val sameWeek = date.get(WeekFields.ISO.weekOfWeekBasedYear()) == now.get(WeekFields.ISO.weekOfWeekBasedYear())
                val sameMonth = date.month == now.month && date.year == now.year
                val sameYear = date.year == now.year

                if (daysBetween == 0L) {
                    volumeDay += volume
                    countDay++
                }
                if (sameWeek && sameYear) {
                    volumeWeek += volume
                    countWeek++
                }
                if (sameMonth) {
                    volumeMonth += volume
                    countMonth++
                }
                if (sameYear) {
                    volumeYear += volume
                    countYear++
                }
            }

            onResult(
                listOf(
                    volumeDay, volumeWeek, volumeMonth, volumeYear,
                    countDay, countWeek, countMonth, countYear
                )
            )
        }
        .addOnFailureListener {
            onResult(
                listOf(0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0)
            )
        }
}


////////////////////////////////////////////////////////////////////////////////////////////////////
// XP to level conversion


fun getLevelFromXp(xp: Double, baseXp: Int = 200, growthRate: Double = 1.12): Int {
    var level = 1
    var totalXpForNextLevel = baseXp

    while (xp >= totalXpForNextLevel) {
        level++
        totalXpForNextLevel += (baseXp * Math.pow(level.toDouble(), growthRate)).toInt()
    }

    return level
}



fun calculateLevelAndRequiredXP(xp: Double, baseXp: Int = 2000  , growthRate: Double = 1.12): Pair<Int, Int> {
    var level = 1
    var totalXpForNextLevel = baseXp

    while (xp >= totalXpForNextLevel) {
        level++
        totalXpForNextLevel += (baseXp * Math.pow(level.toDouble(), growthRate)).toInt()
    }

    return level to (baseXp * Math.pow(level.toDouble(), growthRate)).toInt()
}