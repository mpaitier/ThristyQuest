package com.example.thirstyquest.db

import android.util.Log
import co.yml.charts.common.model.Point
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

////////////////////////////////////////////////////////////////////////////////////////////////////
//      GET

suspend fun getWeekConsumptionPoints(id: String, type: String): List<Point>
{
    if(type == "users" || type == "leagues") {
        val db = FirebaseFirestore.getInstance()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE)

        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        val endOfWeek = today.with(DayOfWeek.SUNDAY)

        val dailyCounts = MutableList(7) { 0 }

        try {
            val pubRefs = db.collection(type)
                .document(id)
                .collection("publications")
                .get()
                .await()

            val publicationIDs = pubRefs.documents.mapNotNull { it.id }

            for (pubID in publicationIDs) {
                try {
                    val pubSnapshot = db.collection("publications")
                        .document(pubID)
                        .get()
                        .await()

                    val dateStr = pubSnapshot.getString("date") ?: continue
                    val date = LocalDate.parse(dateStr, formatter)

                    if (!date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)) {
                        val dayOfWeek = date.dayOfWeek.value  // Lundi = 1, Dimanche = 7
                        val index = (dayOfWeek - 1) % 7
                        dailyCounts[index] += 1
                    }

                } catch (e: Exception) {
                    Log.w("FETCH_PUBLICATION", "Erreur lors de la lecture de la publication $pubID", e)
                }
            }

        } catch (e: Exception) {
            Log.e("FIRESTORE", "Erreur de récupération des IDs de publications : ", e)
        }

        return dailyCounts.mapIndexed { index, count ->
            Point(x = index.toFloat(), y = count.toFloat())
        }
    }
    else {
        Log.e("FIRESTORE", "Type de collection invalide : $type")
        return emptyList()
    }

}

suspend fun getMonthConsumptionPoints(id: String, type: String): List<Point> {
    if(type == "users" || type == "leagues") {
        val db = FirebaseFirestore.getInstance()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE)

        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

        val daysInMonth = today.lengthOfMonth()
        val dailyCounts = MutableList(daysInMonth) { 0 }

        try {
            val pubRefs = db.collection(type)
                .document(id)
                .collection("publications")
                .get()
                .await()

            val publicationIDs = pubRefs.documents.mapNotNull { it.id }

            for (pubID in publicationIDs) {
                try {
                    val pubSnapshot = db.collection("publications")
                        .document(pubID)
                        .get()
                        .await()

                    val dateStr = pubSnapshot.getString("date") ?: continue
                    val date = LocalDate.parse(dateStr, formatter)

                    if (!date.isBefore(startOfMonth) && !date.isAfter(endOfMonth)) {
                        val dayIndex = date.dayOfMonth - 1 // 0-based index
                        dailyCounts[dayIndex] += 1
                    }

                } catch (e: Exception) {
                    Log.w("FETCH_PUBLICATION", "Erreur lecture publication $pubID", e)
                }
            }

        } catch (e: Exception) {
            Log.e("FIRESTORE", "Erreur récupération des IDs de publications : ", e)
        }

        return dailyCounts.mapIndexed { index, count ->
            Point(x = index.toFloat(), y = count.toFloat())
        }
    }
    else {
        Log.e("FIRESTORE", "Type de collection invalide : $type")
        return emptyList()
    }
}

suspend fun getYearConsumptionPoints(id: String, type: String): List<Point>
{
    if(type == "users" || type == "leagues") {
        val db = FirebaseFirestore.getInstance()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE)

        val currentYear = LocalDate.now().year
        val monthlyCounts = MutableList(12) { 0 }

        try {
            val pubRefs = db.collection(type)
                .document(id)
                .collection("publications")
                .get()
                .await()

            val publicationIDs = pubRefs.documents.mapNotNull { it.id }

            for (pubID in publicationIDs) {
                try {
                    val pubSnapshot = db.collection("publications")
                        .document(pubID)
                        .get()
                        .await()

                    val dateStr = pubSnapshot.getString("date") ?: continue
                    val date = LocalDate.parse(dateStr, formatter)

                    if (date.year == currentYear) {
                        val monthIndex = date.monthValue - 1 // 0-based index
                        monthlyCounts[monthIndex] += 1
                    }

                } catch (e: Exception) {
                    Log.w("FETCH_PUBLICATION", "Erreur lecture publication $pubID", e)
                }
            }

        } catch (e: Exception) {
            Log.e("FIRESTORE", "Erreur récupération des IDs de publications : ", e)
        }

        return monthlyCounts.mapIndexed { index, count ->
            Point(x = index.toFloat(), y = count.toFloat())
        }
    }
    else {
        Log.e("FIRESTORE", "Type de collection invalide : $type")
        return emptyList()
    }
}

suspend fun getWeekVolumeConsumptionPoints(id: String, type: String): List<Point>
{
    if(type == "users" || type == "leagues") {
        val db = FirebaseFirestore.getInstance()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE)

        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        val endOfWeek = today.with(DayOfWeek.SUNDAY)

        val dailyVolumes = MutableList(7) { 0.0 }

        try {
            val pubRefs = db.collection(type)
                .document(id)
                .collection("publications")
                .get()
                .await()

            val publicationIDs = pubRefs.documents.mapNotNull { it.id }

            for (pubID in publicationIDs) {
                try {
                    val pubSnapshot = db.collection("publications")
                        .document(pubID)
                        .get()
                        .await()

                    val dateStr = pubSnapshot.getString("date") ?: continue
                    val volume = pubSnapshot.getDouble("volume") ?: continue
                    val date = LocalDate.parse(dateStr, formatter)

                    if (!date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)) {
                        val index = (date.dayOfWeek.value - 1) % 7 // Lundi = 0
                        dailyVolumes[index] += volume
                    }

                } catch (e: Exception) {
                    Log.w("FETCH_PUBLICATION", "Erreur lecture publication $pubID", e)
                }
            }

        } catch (e: Exception) {
            Log.e("FIRESTORE", "Erreur récupération des IDs de publications : ", e)
        }

        return dailyVolumes.mapIndexed { index, volume ->
            Point(x = index.toFloat(), y = volume.toFloat())
        }
    }
    else {
        Log.e("FIRESTORE", "Type de collection invalide : $type")
        return emptyList()
    }
}

suspend fun getMonthVolumeConsumptionPoints(id: String, type: String): List<Point>
{
    if(type == "users" || type == "leagues") {
        val db = FirebaseFirestore.getInstance()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE)

        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

        val daysInMonth = today.lengthOfMonth()
        val dailyVolumes = MutableList(daysInMonth) { 0.0 }

        try {
            val pubRefs = db.collection(type)
                .document(id)
                .collection("publications")
                .get()
                .await()

            val publicationIDs = pubRefs.documents.mapNotNull { it.id }

            for (pubID in publicationIDs) {
                try {
                    val pubSnapshot = db.collection("publications")
                        .document(pubID)
                        .get()
                        .await()

                    val dateStr = pubSnapshot.getString("date") ?: continue
                    val volume = pubSnapshot.getDouble("volume") ?: continue
                    val date = LocalDate.parse(dateStr, formatter)

                    if (!date.isBefore(startOfMonth) && !date.isAfter(endOfMonth)) {
                        val index = date.dayOfMonth - 1 // 0-based
                        dailyVolumes[index] += volume
                    }

                } catch (e: Exception) {
                    Log.w("FETCH_PUBLICATION", "Erreur lecture publication $pubID", e)
                }
            }

        } catch (e: Exception) {
            Log.e("FIRESTORE", "Erreur récupération des IDs de publications : ", e)
        }

        return dailyVolumes.mapIndexed { index, volume ->
            Point(x = index.toFloat(), y = volume.toFloat())
        }
    }
    else {
        Log.e("FIRESTORE", "Type de collection invalide : $type")
        return emptyList()
    }
}

suspend fun getYearVolumeConsumptionPoints(id: String, type: String): List<Point> {
    if(type == "users" || type == "leagues") {
        val db = FirebaseFirestore.getInstance()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE)

        val currentYear = LocalDate.now().year
        val monthlyVolumes = MutableList(12) { 0.0 }

        try {
            val pubRefs = db.collection(type)
                .document(id)
                .collection("publications")
                .get()
                .await()

            val publicationIDs = pubRefs.documents.mapNotNull { it.id }

            for (pubID in publicationIDs) {
                try {
                    val pubSnapshot = db.collection("publications")
                        .document(pubID)
                        .get()
                        .await()

                    val dateStr = pubSnapshot.getString("date") ?: continue
                    val volume = pubSnapshot.getDouble("volume") ?: continue

                    val date = LocalDate.parse(dateStr, formatter)

                    if (date.year == currentYear) {
                        val monthIndex = date.monthValue - 1 // 0-based index
                        monthlyVolumes[monthIndex] += volume
                    }

                } catch (e: Exception) {
                    Log.w("FETCH_PUBLICATION", "Erreur lecture publication $pubID", e)
                }
            }

        } catch (e: Exception) {
            Log.e("FIRESTORE", "Erreur récupération des IDs de publications : ", e)
        }

        return monthlyVolumes.mapIndexed { index, volume ->
            Point(x = index.toFloat(), y = volume.toFloat())
        }
    }
    else {
        Log.e("FIRESTORE", "Type de collection invalide : $type")
        return emptyList()
    }
}