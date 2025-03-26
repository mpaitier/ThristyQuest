package com.example.thirstyquest.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

fun addLeagueToFirestore(uid: String, name: String) {
    val db = FirebaseFirestore.getInstance()
    val league = hashMapOf(
        "owner uid" to uid,
        "name" to name,
        "xp" to 0
    )

    db.collection("leagues").document(uid)
        .set(league)
        .addOnSuccessListener {
            Log.d("FIRESTORE", "Ligue ajoutée !")
        }
        .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur d'ajout : ", e) }
}

fun joinLeague(uid: String, lid: String, leagueName: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users").document(uid).collection("leagues").document(lid)
        .set(hashMapOf("League name" to leagueName))
        .addOnSuccessListener { Log.d("FIRESTORE", "Ligue rejointe !") }
        .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur lors de la jointure : ", e) }
}