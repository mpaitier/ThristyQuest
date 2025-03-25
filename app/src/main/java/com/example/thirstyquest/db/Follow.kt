package com.example.thirstyquest.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

fun checkIfFriend(currentUid: String, friendId: String, onResult: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(currentUid)
        .collection("following").document(friendId)
        .get()
        .addOnSuccessListener { document ->
            onResult(document.exists())
        }
        .addOnFailureListener {
            onResult(false)
        }
}

fun toggleFriend(currentUid: String, friendId: String, isFriend: Boolean, onComplete: () -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.runBatch { batch ->
        val followingRef = db.collection("users").document(currentUid).collection("following").document(friendId)
        val followerRef = db.collection("users").document(friendId).collection("followers").document(currentUid)

        if (isFriend) {
            // Supprimer l'ami
            batch.delete(followingRef)
            batch.delete(followerRef)
        } else {
            // Ajouter l'ami
            val data = hashMapOf("timestamp" to System.currentTimeMillis())
            batch.set(followingRef, data)
            batch.set(followerRef, data)
        }
    }.addOnSuccessListener {
        onComplete()
    }.addOnFailureListener { e ->
        Log.e("FIRESTORE", "Erreur lors du suivi/désabonnement", e)
    }
}



fun getFollowers(uid: String, onResult: (List<String>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(uid).collection("followers")
        .get()
        .addOnSuccessListener { snapshot ->
            val followers = snapshot.documents.map { it.id }
            onResult(followers)
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la récupération des followers", e)
            onResult(emptyList())
        }
}

fun getFollowing(uid: String, onResult: (List<String>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(uid).collection("following")
        .get()
        .addOnSuccessListener { snapshot ->
            val followers = snapshot.documents.map { it.id }
            onResult(followers)
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la récupération des followers", e)
            onResult(emptyList())
        }
}


/*
fun followUser(currentUid: String, targetUid: String) {
    val db = FirebaseFirestore.getInstance()

    // Ajouter currentUid dans la liste des followers de targetUid
    val followerRef = db.collection("users").document(targetUid)
        .collection("followers").document(currentUid)

    // Ajouter targetUid dans la liste des following de currentUid
    val followingRef = db.collection("users").document(currentUid)
        .collection("following").document(targetUid)

    val followData = hashMapOf("timestamp" to System.currentTimeMillis())

    // Exécuter les deux opérations en parallèle
    db.runBatch { batch ->
        batch.set(followerRef, followData)
        batch.set(followingRef, followData)
    }.addOnSuccessListener {
        Log.d("FIRESTORE", "Utilisateur suivi avec succès !")
    }.addOnFailureListener { e ->
        Log.e("FIRESTORE", "Erreur lors du suivi", e)
    }
}
*/
/*
fun unfollowUser(currentUid: String, targetUid: String) {
    val db = FirebaseFirestore.getInstance()

    val followerRef = db.collection("users").document(targetUid)
        .collection("followers").document(currentUid)

    val followingRef = db.collection("users").document(currentUid)
        .collection("following").document(targetUid)

    db.runBatch { batch ->
        batch.delete(followerRef)
        batch.delete(followingRef)
    }.addOnSuccessListener {
        Log.d("FIRESTORE", "Désabonnement réussi !")
    }.addOnFailureListener { e ->
        Log.e("FIRESTORE", "Erreur lors du désabonnement", e)
    }
}
*/