package com.example.thirstyquest.db
import android.util.Log
import com.example.thirstyquest.data.DrinkCategories
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

object DrinkPointManager {

    private val drinkDocRef = FirebaseFirestore.getInstance()
        .collection("drinkPoints")
        .document("current")

    fun checkAndUpdateDrinkPointsIfNeeded() {
        drinkDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val lastUpdate = document.getTimestamp("last_time")?.toDate()
                val now = Date()

                val hoursDiff = (now.time - (lastUpdate?.time ?: 0)) / (1000 * 60 * 60)
                Log.d("DrinkPointManager", "Last update was $hoursDiff hours ago")

                if (hoursDiff >= 3) {
                    updateDrinkPoints()
                } else {
                    Log.d("DrinkPointManager", "Pas besoin de mettre à jour les points")
                }
            } else {
                // Document doesn't exist? Create it for the first time
                updateDrinkPoints()
            }
        }.addOnFailureListener {
            Log.e("DrinkPointManager", "Erreur lecture drinkPoints: ${it.message}")
        }
    }

    private fun updateDrinkPoints() {
        val newPoints = generateRandomDrinkPoints()
        drinkDocRef.set(newPoints)
            .addOnSuccessListener {
                Log.d("DrinkPointManager", "Points des boissons mis à jour : $newPoints")
            }
            .addOnFailureListener {
                Log.e("DrinkPointManager", "Erreur lors de la mise à jour : ${it.message}")
            }
    }

    private fun generateRandomDrinkPoints(): Map<String, Any> {
        val range = 50..500

        val drinkPoints = DrinkCategories.basePoints.mapValues { (drink, _) ->
            if (drink == "Sans alcool") 10 else range.random()
            if (drink == "Autre alcool") 50 else range.random()
        }
        return drinkPoints + mapOf("last_time" to Timestamp.now())
    }


    suspend fun getTopDrinksFromFirestore(): List<Pair<String, Int>> {
        val db = FirebaseFirestore.getInstance()
        val result = mutableListOf<Pair<String, Int>>()

        return try {
            val snapshot = db.collection("drinkPoints").document("current").get().await()
            val ignoredFields = listOf("Sans alcool", "last_time")

            for ((key, value) in snapshot.data ?: emptyMap()) {
                if (key !in ignoredFields && value is Number) {
                    result.add(key to value.toInt())
                }
            }

            // Trie les boissons par points décroissants et retourne les 3 meilleures
            result.sortedByDescending { it.second }.take(3)
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Erreur récupération top boissons", e)
            emptyList()
        }
    }

    suspend fun getAllDrinksFromFirestore(): List<Pair<String, Int>> {
        val db = FirebaseFirestore.getInstance()
        val result = mutableListOf<Pair<String, Int>>()
        val ignoredFields = listOf("Sans alcool", "last_time")

        return try {
            val snapshot = db.collection("drinkPoints").document("current").get().await()
            for ((key, value) in snapshot.data ?: emptyMap()) {
                if (key !in ignoredFields && value is Number) {
                    result.add(key to value.toInt())
                }
            }
            result.sortedByDescending { it.second } // trie, mais ne coupe pas
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Erreur récupération boissons", e)
            emptyList()
        }
    }

}
