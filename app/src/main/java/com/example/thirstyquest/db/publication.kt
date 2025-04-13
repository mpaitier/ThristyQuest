package com.example.thirstyquest.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.text.SimpleDateFormat
import android.graphics.Bitmap
import com.example.thirstyquest.data.Publication
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*
import com.example.thirstyquest.data.DrinkCategories
import com.example.thirstyquest.data.DrinkVolumes
import com.google.firebase.firestore.SetOptions
import kotlin.math.max

////////////////////////////////////////////////////////////////////////////////////////////////////
//    POST

suspend fun addPublicationToFirestore(userId: String, drinkName: String, drinkPrice: String, drinkCategory: String, drinkVolume: Int, photoUrl: String): Pair<String,Int> {
    val db = FirebaseFirestore.getInstance()
    val id = UUID.randomUUID().toString() // Génère un ID unique
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    val currentTime = timeFormat.format(Date())
    val drinkPointsSnapshot = db.collection("drinkPoints").document("current").get().await()
    val basePoints = (drinkPointsSnapshot.getDouble(drinkCategory) ?: 0.0).toInt()
    val multiplier = DrinkVolumes.volumeMultipliers[drinkVolume] ?: 1.0
    val points = (basePoints * multiplier).toInt()

    val publication = hashMapOf(
        "ID" to id,
        "description" to drinkName,
        "user_ID" to userId,
        "date" to currentDate,
        "hour" to currentTime,
        "category" to drinkCategory,
        "volume" to drinkVolume.toDouble()/100,
        "price" to (drinkPrice.toDoubleOrNull() ?: 0.0),
        "photo" to photoUrl,
        "points" to points
    )

    db.collection("publications")
        .document(id)
        .set(publication)
        .addOnSuccessListener { Log.d("Firebase", "Publication ajoutée avec succès") }
        .addOnFailureListener { e -> Log.w("Firebase", "Erreur lors de l'ajout", e) }

    db.collection("users")
        .document(userId)
        .collection("publications")
        .document(id)
        .set(hashMapOf("date" to currentDate, "hour" to currentTime))

    db.collection("users").document(userId)
        .update("xp", FieldValue.increment(points.toDouble()))
        .addOnSuccessListener { Log.d("Firebase", "Volume total mis à jour avec succès") }
        .addOnFailureListener { e -> Log.w("Firebase", "Erreur lors de la mise à jour du volume total", e) }

    db.collection("users").document(userId)
        .update("total drink", FieldValue.increment((drinkVolume.toDouble()/100)))
        .addOnSuccessListener { Log.d("Firebase", "Volume total mis à jour avec succès") }
        .addOnFailureListener { e -> Log.w("Firebase", "Erreur lors de la mise à jour du volume total", e) }

    val userRef = db.collection("users").document(userId)
    val data1 = mapOf(
        "total paid" to FieldValue.increment(drinkPrice.toDoubleOrNull() ?: 0.0),
        )
    userRef.set(data1, SetOptions.merge())
        .addOnSuccessListener {
            Log.d("Firebase", "Document mis à jour (ou créé) avec succès")
        }
        .addOnFailureListener { e ->
            Log.w("Firebase", "Erreur lors de la mise à jour du prix total", e)
        }
    // TODO : faire systeme de level de boisson en fonction des points
    val categoryRef = db.collection("users").document(userId).collection("category").document(drinkCategory)
    val data2 = mapOf(
        "total" to FieldValue.increment(1),
        "point" to FieldValue.increment((points.toDouble())),
        "level" to FieldValue.increment(0),
    )
    categoryRef.set(data2, SetOptions.merge())
        .addOnSuccessListener {
            Log.d("Firebase", "Document mis à jour (ou créé) avec succès")
        }
        .addOnFailureListener { e ->
            Log.w("Firebase", "Erreur lors de la mise à jour ou création de category", e)
        }
    

    return Pair(id, points)
}

fun addPublicationToLeague(pid : String, lid: String, price : Double, volume: Double, points: Int, category: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("leagues").document(lid).collection("publications").document(pid)
        .set(hashMapOf("pid" to pid))
        .addOnSuccessListener { Log.d("Firebase", "Publication ajoutée à la ligue avec succès") }
        .addOnFailureListener { e -> Log.w("Firebase", "Erreur lors de l'ajout", e) }

    // Incrementer les valeurs "total liters", "total price" et "category" de la ligue
    db.collection("leagues").document(lid)
        .update("total liters", FieldValue.increment(volume/100))
        .addOnSuccessListener { Log.d("Firebase", "Volume total mis à jour avec succès") }
        .addOnFailureListener { e -> Log.w("Firebase", "Erreur lors de la mise à jour du volume total", e) }

    db.collection("leagues").document(lid)
        .update("total price", FieldValue.increment(price))
        .addOnSuccessListener { Log.d("Firebase", "Prix total mis à jour avec succès") }
        .addOnFailureListener { e -> Log.w("Firebase", "Erreur lors de la mise à jour du prix total", e) }

    db.collection("leagues").document(lid)
        .update("xp", FieldValue.increment(points.toDouble()))
        .addOnSuccessListener { Log.d("Firebase", "Prix total mis à jour avec succès") }
        .addOnFailureListener { e -> Log.w("Firebase", "Erreur lors de la mise à jour du prix total", e) }


    val categoryRef = db.collection("leagues").document(lid).collection("category").document(category)
    val data = mapOf(
        "total" to FieldValue.increment(1),
        "total liters" to FieldValue.increment((volume / 100))
    )
    categoryRef.set(data, SetOptions.merge())
        .addOnSuccessListener {
            Log.d("Firebase", "Document mis à jour (ou créé) avec succès")
        }
        .addOnFailureListener { e ->
            Log.w("Firebase", "Erreur lors de la mise à jour ou création de category", e)
        }
}

suspend fun uploadImageToFirebase(userId: String, bitmap: Bitmap): String? {
    Log.d("UPLOAD", "Début de l'upload image...")

    return try {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${userId}_${UUID.randomUUID()}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        imageRef.putBytes(imageData).await()
        Log.d("UPLOAD", "Image bien uploadée")

        val downloadUrl = imageRef.downloadUrl.await().toString()
        Log.d("UPLOAD", "URL image : $downloadUrl")

        return downloadUrl
    } catch (e: Exception) {
        Log.e("UPLOAD_ERROR", "Erreur upload", e)
        null
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//    GET

suspend fun getTotalDrinkVolume(userID: String): Double {
    val db = FirebaseFirestore.getInstance()

    return try {
        val snapshot = db.collection("users")
            .document(userID)
            .get()
            .await()

        snapshot.getDouble("total drink") ?: 0.0
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération du total drink :", e)
        0.0
    }
}


suspend fun getTotalMoneySpent(userID: String): Double {
    val db = FirebaseFirestore.getInstance()
    var totalSpent = 0.0

    return try {
        val result = db.collection("publications")
            .whereEqualTo("user_ID", userID)
            .get()
            .await()

        for (document in result) {
            val price = document.getDouble("price") ?: 0.0
            totalSpent += price
        }

        totalSpent
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération du total dépensé : ", e)
        0.0
    }
}

suspend fun getPublicationCountByCategory(category: String, userID: String): Int {
    val db = FirebaseFirestore.getInstance()
    var count = 0

    return try {
        val result = db.collection("publications")
            .whereEqualTo("user_ID", userID)
            .whereEqualTo("category", category)
            .get()
            .await()

        count = result.size()

        count
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération du nombre de publications : ", e)
        0
    }
}

suspend fun getAverageDrinkConsumption(period: String,userID:String): Double {
    val db = FirebaseFirestore.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    return try {
        val result = db.collection("publications")
            .whereEqualTo("user_ID", userID)
            .get()
            .await()

        if (result.isEmpty) return 0.0

        val publicationDates = mutableListOf<Date>()
        for (document in result) {
            val dateString = document.getString("date")
            dateString?.let {
                dateFormat.parse(it)?.let { date -> publicationDates.add(date) }
            }
        }

        if (publicationDates.isEmpty()) return 0.0

        // Trouver la première et la dernière publication
        val firstDate = publicationDates.minByOrNull { it.time } ?: return 0.0
        val lastDate = publicationDates.maxByOrNull { it.time } ?: return 0.0

        val calendar = Calendar.getInstance()

        calendar.time = firstDate
        val startYear = calendar.get(Calendar.YEAR)
        val startMonth = calendar.get(Calendar.MONTH)
        val startDay = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.time = lastDate
        val endYear = calendar.get(Calendar.YEAR)
        val endMonth = calendar.get(Calendar.MONTH)
        val endDay = calendar.get(Calendar.DAY_OF_YEAR)

        val totalPeriods = when (period.uppercase(Locale.ROOT)) {
            "DAY" -> max((endYear - startYear) * 365 + (endDay - startDay) + 1, 1)
            "MONTH" -> max((endYear - startYear) * 12 + (endMonth - startMonth) + 1, 1)
            "YEAR" -> max((endYear - startYear) + 1, 1)
            else -> return 0.0 // Paramètre invalide
        }

        return result.size().toDouble() / totalPeriods

    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur lors du calcul de la consommation", e)
        0.0
    }
}

suspend fun getLeaguePublications(leagueID: String): List<Publication> {
    val db = FirebaseFirestore.getInstance()
    val publications = mutableListOf<Publication>()

    return try {
        val result = db.collection("leagues")
            .document(leagueID)
            .collection("publications")
            .get()
            .await()

        for (document in result) {
            // for each publication
            val pid = document.getString("pid") ?: continue
            val pubDoc = db.collection("publications").document(pid).get().await()

            val publication = Publication(
                ID = "", // ← ou génère un ID local si besoin
                description = pubDoc.getString("description") ?: "",
                user_ID = pubDoc.getString("user_ID") ?: "",
                date = pubDoc.getString("date") ?: "",
                hour = pubDoc.getString("hour") ?: "",
                category = pubDoc.getString("category") ?: "",
                price = pubDoc.getDouble("price") ?: 0.0,
                photo = pubDoc.getString("photo") ?: "",
                points = pubDoc.getLong("points")?.toInt() ?: 0
            )
            publications.add(publication)
        }

        publications
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des publications de la ligue : ", e)
        emptyList()
    }
}

fun getUserLastPublications(userID: String, onResult: (List<Publication>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val publications = mutableListOf<Publication>()

    db.collection("users")
        .document(userID)
        .collection("publications")
        .addSnapshotListener { userPubSnapshots, error ->
            if (error != null) {
                Log.e("FIRESTORE", "Erreur lors de l'écoute des publications utilisateur : ", error)
                onResult(emptyList())
                return@addSnapshotListener
            }

            if (userPubSnapshots == null || userPubSnapshots.isEmpty) {
                onResult(emptyList())
                return@addSnapshotListener
            }

            // Vide les anciennes publications
            publications.clear()

            val pubRefs = userPubSnapshots.documents
            var fetched = 0

            for (docRef in pubRefs) {
                val pubID = docRef.id

                db.collection("publications")
                    .document(pubID)
                    .get()
                    .addOnSuccessListener { pubDoc ->
                        val publication = Publication(
                            ID = "",
                            description = pubDoc.getString("description") ?: "",
                            user_ID = pubDoc.getString("user_ID") ?: "",
                            date = pubDoc.getString("date") ?: "",
                            hour = pubDoc.getString("hour") ?: "",
                            category = pubDoc.getString("category") ?: "",
                            price = pubDoc.getDouble("price") ?: 0.0,
                            photo = pubDoc.getString("photo") ?: "",
                            points = pubDoc.getLong("points")?.toInt() ?: 0
                        )
                        publications.add(publication)
                        fetched++

                        // Une fois qu'on a tout récupéré, on retourne la liste
                        if (fetched == pubRefs.size) {
                            // Tu peux aussi trier ici par date/heure si besoin
                            onResult(publications.sortedByDescending { it.date + it.hour })
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FIRESTORE", "Erreur de récupération de publication $pubID", e)
                        fetched++
                        if (fetched == pubRefs.size) {
                            onResult(publications.sortedByDescending { it.date + it.hour })
                        }
                    }
            }
        }
}
suspend fun getFriendPublications(friendId: String): List<Pair<String, String>> {
    val db = FirebaseFirestore.getInstance()
    val publications = mutableListOf<Pair<String, String>>()

    try {
        val result = db.collection("publications")
            .whereEqualTo("user_ID", friendId)
            .orderBy("date", Query.Direction.DESCENDING)
            .orderBy("hour", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()

        // Récupérer les publications sous forme de Paires (description, points)
        for (document in result) {
            val description = document.getString("description") ?: ""
            val points = document.getLong("points")?.toString() ?: "0"
            publications.add(Pair(description, points))
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Erreur de récupération des publications", e)
    }

    return publications
}
