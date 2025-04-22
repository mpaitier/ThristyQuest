package com.example.thirstyquest.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

////////////////////////////////////////////////////////////////////////////////////////////////////
//      POST - PUT

fun toggleFollow(currentUid: String, friendId: String, isFriend: Boolean, onComplete: () -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.runBatch { batch ->
        val followingRef = db.collection("users").document(currentUid).collection("following").document(friendId)
        val followerRef = db.collection("users").document(friendId).collection("followers").document(currentUid)

        if (isFriend) {
            // Unfollow
            batch.delete(followingRef)
            batch.delete(followerRef)
        } else {
            // Follow
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

////////////////////////////////////////////////////////////////////////////////////////////////////
//      GET

fun checkIfFollowing(currentUid: String, friendId: String, onResult: (Boolean) -> Unit) {
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

suspend fun getFollowerCount(uid: String): Int {
    val db = FirebaseFirestore.getInstance()
    return try {
        val snapshot = db.collection("users").document(uid).collection("followers").get().await()
        snapshot.documents.count { !it.contains("initialized") }
    } catch (e: Exception) {
        Log.e("FIRESTORE_DEBUG", "Erreur Firestore: ${e.message}")
        0
    }
}

suspend fun getFollowingCount(uid: String): Int {
    val db = FirebaseFirestore.getInstance()
    return try {
        val snapshot = db.collection("users").document(uid).collection("following").get().await()
        snapshot.documents.count { !it.contains("initialized") }
    } catch (e: Exception) {
        Log.e("FIRESTORE_DEBUG", "Erreur Firestore: ${e.message}")
        0
    }
}

suspend fun getAllFollowingIdCoroutine(uid: String): List<String> {
    val db = FirebaseFirestore.getInstance()
    val friendList = mutableListOf<String>()
    try {
        val result = db.collection("users").document(uid).collection("following")
            .get()
            .await()
        for (document in result) {
            if (!document.contains("initialized")) {
                friendList.add(document.id)
            }
        }
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return friendList
}

fun getAllfollowingIdSnap(uid: String, onFriendsUpdate: (List<String>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .document(uid)
        .collection("following")
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FIRESTORE", "Erreur de récupération en temps réel : ", error)
                return@addSnapshotListener
            }
            val filteredFriends = snapshot?.documents
                ?.filter { !it.contains("initialized") }
                ?.map { it.id }
                ?: emptyList()

            onFriendsUpdate(filteredFriends)
        }
}

suspend fun getAllFollowersIdCoroutine(uid: String): List<String> {
    val db = FirebaseFirestore.getInstance()
    val friendList = mutableListOf<String>()
    try {
        val result = db.collection("users").document(uid).collection("followers")
            .get()
            .await()
        for (document in result) {
            if (!document.contains("initialized")) {
                friendList.add(document.id)
            }
        }
    } catch (e: Exception) {
        Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
    }
    return friendList
}
