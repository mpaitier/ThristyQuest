package com.example.thirstyquest.db

import android.util.Log
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

fun addPublicationToFirestore(userId: String, drinkName: String, drinkPrice: String, drinkCategory: String, drinkVolume: Int) {
    val db = FirebaseFirestore.getInstance()
    val id = UUID.randomUUID().toString() // Génère un ID unique
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    val currentTime = timeFormat.format(Date())
    val points = 500

    val publication = hashMapOf(
        "ID" to id,
        "description" to drinkName,
        "user_ID" to userId,
        "date" to currentDate,
        "hour" to currentTime,
        "category" to drinkCategory,
        "volume" to drinkVolume,
        "price" to (drinkPrice.toDoubleOrNull() ?: 0.0),
        "photo" to "",
        "points" to points
    )

    db.collection("publications")
        .document(id)
        .set(publication)
        .addOnSuccessListener { Log.d("Firebase", "Publication ajoutée avec succès") }
        .addOnFailureListener { e -> Log.w("Firebase", "Erreur lors de l'ajout", e) }
}


suspend fun getTotalDrinkVolume(authViewModel: AuthViewModel): Int {
    val db = FirebaseFirestore.getInstance()
    var totalVolume = 0

    return try {
        val result = db.collection("publications")
            .whereEqualTo("user_ID",authViewModel.uid)
            .get()
            .await()

        for (document in result) {
            val volume = document.getLong("volume")?.toInt() ?: 0
            totalVolume += volume
        }

        totalVolume
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des volumes : ", e)
        0
    }
}

suspend fun getTotalMoneySpent(authViewModel: AuthViewModel): Double {
    val db = FirebaseFirestore.getInstance()
    var totalSpent = 0.0

    return try {
        val result = db.collection("publications")
            .whereEqualTo("user_ID", authViewModel.uid)
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

suspend fun getPublicationCountByCategory(authViewModel: AuthViewModel, category: String): Int {
    val db = FirebaseFirestore.getInstance()
    var count = 0

    return try {
        val result = db.collection("publications")
            .whereEqualTo("user_ID", authViewModel.uid)
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
suspend fun getAverageDrinkConsumption(authViewModel: AuthViewModel, period: String): Double {
    val db = FirebaseFirestore.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    return try {
        val result = db.collection("publications")
            .whereEqualTo("user_ID", authViewModel.uid)
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
