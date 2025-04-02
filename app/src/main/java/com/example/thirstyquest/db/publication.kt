package com.example.thirstyquest.db

import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import com.example.thirstyquest.ui.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

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
