package com.example.thirstyquest.db

import android.util.Log
import com.example.thirstyquest.data.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun getCollectionUser(userID: String): List<Category> {
    val db = FirebaseFirestore.getInstance()
    val categories = mutableListOf<Category>()

    return try {
        val result = db.collection("users")
            .document(userID)
            .collection("category")
            .get()
            .await()

        for (document in result) {
            val name = document.id

            val level = document.getLong("level")?.toInt() ?: 0
            val points = document.getDouble("points") ?: 0.0
            val total = document.getDouble("total") ?: 0.0

            val category = Category(
                name = name,
                level = level,
                points = points,
                total = total
            )
            categories.add(category)
        }

        categories.sortedByDescending { it.level }
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur lors de la récupération des catégories :", e)
        emptyList()
    }
}
