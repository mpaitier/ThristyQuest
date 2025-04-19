package com.example.thirstyquest.ui.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thirstyquest.db.addUserToFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.example.thirstyquest.db.doesUsernameExist
import com.example.thirstyquest.db.uploadImageToFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// from : https://www.youtube.com/watch?v=KOnLpNZ4AFc&ab_channel=EasyTuto
class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // UID de l'utilisateur connecté
    private val _uid = MutableLiveData<String?>()
    val uid: LiveData<String?> = _uid

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                _uid.value = user.uid
                _authState.value = AuthState.Authenticated
            } else {
                _uid.value = null
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun signin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Veuillez remplir tous les champs")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uid.value = auth.currentUser?.uid
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Erreur de connexion")
                }
            }
    }

    fun signup(email: String, password: String, pseudo: String, profileImageUrl: String? = null) {
        if (email.isEmpty() || password.isEmpty() || pseudo.isEmpty()) {
            _authState.value = AuthState.Error("Veuillez remplir tous les champs")
            return
        }

        if (pseudo.length !in 3..20) {
            _authState.value = AuthState.Error("Le pseudo doit contenir entre 3 et 20 caractères")
            return
        }

        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error("Adresse email non valide")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                if (doesUsernameExist(pseudo)) {
                    _authState.value = AuthState.Error("Pseudo déjà utilisé")
                    return@launch
                }

                when (isValidPassword(password)) {
                    PasswordValidationResult.Valid -> {
                        try {
                            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                            val user = authResult.user

                            user?.updateProfile(
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(pseudo)
                                    .build()
                            )?.await()

                            addUserToFirestore(user!!.uid, pseudo, profileImageUrl)
                            _uid.value = user.uid
                            _authState.value = AuthState.Authenticated
                        } catch (e: FirebaseAuthUserCollisionException) {
                            _authState.value = AuthState.Error("Cette adresse email est déjà utilisée")
                        } catch (e: Exception) {
                            _authState.value = AuthState.Error(e.message ?: "Erreur inconnue")
                        }
                    }
                    PasswordValidationResult.TooShort -> _authState.value = AuthState.Error("Mot de passe trop court")
                    PasswordValidationResult.MissingDigit -> _authState.value = AuthState.Error("Il manque un chiffre")
                    PasswordValidationResult.MissingUpperCase -> _authState.value = AuthState.Error("Il manque une majuscule")
                    PasswordValidationResult.MissingLowerCase -> _authState.value = AuthState.Error("Il manque une minuscule")
                    PasswordValidationResult.MissingSymbol -> _authState.value = AuthState.Error("Il manque un symbole")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Erreur de vérification du pseudo : ${e.message}")
            }
        }
    }

    fun uploadProfileImageAndSignup(email: String, password: String, pseudo: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val imageUrl = uploadImageToFirebase(pseudo, uri, context)
                signup(email = email, password = password, pseudo = pseudo, profileImageUrl = imageUrl)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Erreur lors de l'upload de la photo : ${e.message}")
            }
        }
    }

    fun uploadProfileImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            val currentUid = auth.currentUser?.uid ?: return@launch
            val url = uploadImageToFirebase(currentUid, uri, context)
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(currentUid)
                .update("photoUrl", url ?: "")
                .addOnSuccessListener { Log.d("PROFILE", "Photo ajoutée") }
                .addOnFailureListener { Log.e("PROFILE", "Erreur d'ajout photo", it) }
        }
    }


    fun signout() {
        auth.signOut()
        _uid.value = null
        _authState.value = AuthState.Unauthenticated
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): PasswordValidationResult {
        val minLength = 8
        val hasDigit = password.any { it.isDigit() }
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }

        return when {
            password.length < minLength -> PasswordValidationResult.TooShort
            !hasDigit -> PasswordValidationResult.MissingDigit
            !hasUpperCase -> PasswordValidationResult.MissingUpperCase
            !hasLowerCase -> PasswordValidationResult.MissingLowerCase
            !hasSymbol -> PasswordValidationResult.MissingSymbol
            else -> PasswordValidationResult.Valid
        }
    }

    sealed class PasswordValidationResult {
        object Valid : PasswordValidationResult()
        object TooShort : PasswordValidationResult()
        object MissingDigit : PasswordValidationResult()
        object MissingUpperCase : PasswordValidationResult()
        object MissingLowerCase : PasswordValidationResult()
        object MissingSymbol : PasswordValidationResult()
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}


