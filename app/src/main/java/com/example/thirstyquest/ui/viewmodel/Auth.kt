package com.example.thirstyquest.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest

// from : https://www.youtube.com/watch?v=KOnLpNZ4AFc&ab_channel=EasyTuto
class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus()
    {
        if(auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun signin(email : String, password : String)
    {
        if(email.isEmpty() || password.isEmpty())
        {
            _authState.value = AuthState.Error("Veuillez remplir tous les champs")
            return
        }

        // we we are login in we are first loading
        _authState.value = AuthState.Loading
        // we try to login
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // if we successfully login, we are authenticated
                if(task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?: "Quelque chose s'est mal passé")
                }
            }
    }

    fun singup(email: String, password: String, pseudo: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Veuillez remplir tous les champs")
            return
        }

        if (pseudo.length < 3 || pseudo.length > 20) {
            _authState.value = AuthState.Error("Le pseudo doit contenir entre 3 et 20 caractères")
            return
        }

        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error("Adresse email non valide")
            return
        }

        when (isValidPassword(password)) {
            PasswordValidationResult.Valid -> {
                // if we are signing up we are first loading
               val user = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(pseudo)
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _authState.value = AuthState.Authenticated
                        } else {
                            _authState.value = AuthState.Error("Nom d'utilisateur déjà utilisé")
                        }
                    }

                // we try to sign up
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        // if we successfully sign up, we are authenticated
                        if (task.isSuccessful) {
                            _authState.value = AuthState.Authenticated
                        } else {
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                _authState.value = AuthState.Error("Cette adresse email est déjà utilisée")
                            } else {
                                _authState.value =
                                    AuthState.Error(task.exception?.message ?: "Quelque chose s'est mal passé")
                            }
                        }
                    }
            }
            PasswordValidationResult.TooShort -> _authState.value = AuthState.Error("Mot de passe trop court")
            PasswordValidationResult.MissingDigit -> _authState.value = AuthState.Error("Il manque un chiffre")
            PasswordValidationResult.MissingUpperCase -> _authState.value = AuthState.Error("Il manque une majuscule")
            PasswordValidationResult.MissingLowerCase -> _authState.value = AuthState.Error("Il manque une minuscule")
            PasswordValidationResult.MissingSymbol -> _authState.value = AuthState.Error("Il manque un symbole")
        }
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

    fun signout()
    {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}