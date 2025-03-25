package com.example.thirstyquest.db

import com.google.firebase.firestore.FirebaseFirestore

//-----------------------------------------------------------------------------------------------------------------------------
/*
fun getUsersFromFirestore() {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject(User::class.java)
                Log.d("FIRESTORE", "Utilisateur récupéré : $user")
            }
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur de récupération des utilisateurs : ", e)
        }
}
fun updateUserLevel(userId: String, newLevel: Int) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .document(userId)
        .update("level", newLevel)
        .addOnSuccessListener {
            Log.d("FIRESTORE", "Niveau mis à jour avec succès !")
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la mise à jour du niveau : ", e)
        }
}
fun deleteUserFromFirestore(userId: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .document(userId)
        .delete()
        .addOnSuccessListener {
            Log.d("FIRESTORE", "Utilisateur supprimé avec succès !")
        }
        .addOnFailureListener { e ->
            Log.e("FIRESTORE", "Erreur lors de la suppression de l'utilisateur : ", e)
        }
}
fun sendFriendRequest(fromUserId: String, toUserId: String) {
    val db = FirebaseFirestore.getInstance()

    val request = hashMapOf("from" to fromUserId, "status" to "pending")

    db.collection("users").document(toUserId)
        .collection("friend_requests")
        .document(fromUserId)
        .set(request)
        .addOnSuccessListener { Log.d("FIRESTORE", "Demande envoyée !") }
        .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur : ", e) }
}
fun acceptFriendRequest(currentUserId: String, friendUserId: String) {
    val db = FirebaseFirestore.getInstance()

    val friendship = hashMapOf("since" to System.currentTimeMillis())

    db.collection("users").document(currentUserId)
        .collection("friends").document(friendUserId)
        .get()
        .addOnSuccessListener { document ->
            if (!document.exists()) {
                db.collection("users").document(currentUserId)
                    .collection("friends").document(friendUserId)
                    .set(friendship)

                db.collection("users").document(friendUserId)
                    .collection("friends").document(currentUserId)
                    .set(friendship)

                db.collection("users").document(currentUserId)
                    .collection("friend_requests").document(friendUserId)
                    .delete()
                    .addOnSuccessListener { Log.d("FIRESTORE", "Ami ajouté !") }
                    .addOnFailureListener { e -> Log.e("FIRESTORE", "Erreur : ", e) }
            } else {
                Log.d("FIRESTORE", "Cette amitié existe déjà.")
            }
        }
}
fun getFriends(userId: String, onResult: (List<User>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users").document(userId)
        .collection("friends")
        .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FIRESTORE", "Erreur : ", e)
                return@addSnapshotListener
            }

            val friends = mutableListOf<User>()
            snapshot?.documents?.forEach { doc ->
                val friendId = doc.id

                db.collection("users").document(friendId).get()
                    .addOnSuccessListener { friendDoc ->
                        val friend = friendDoc.toObject(User::class.java)
                        friend?.let { friends.add(it) }
                        onResult(friends)  // Mettre à jour la liste
                    }
            }
        }
}
*/
