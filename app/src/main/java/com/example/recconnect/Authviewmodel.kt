package com.example.recconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    // Firebase Auth persists the session itself, so this survives app restarts
    val isLoggedIn: Boolean = Firebase.auth.currentUser != null
)

class AuthViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val first = firstName.trim()
        val last = lastName.trim()
        val mail = email.trim().lowercase()

        validate(first, last, mail, password, confirmPassword)?.let { msg ->
            _uiState.update { it.copy(error = msg) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1. Create the login (Firebase hashes/stores the password)
                val result = auth.createUserWithEmailAndPassword(mail, password).await()
                val user = result.user ?: throw IllegalStateException("No user returned")

                // 2. Create the profile doc in the same "users" collection/shape the team uses,
                //    using the auth uid as the document id so it's easy to look up later
                val profile = hashMapOf(
                    "name" to "$first $last",
                    "email" to mail,
                    "events" to ArrayList<Int>()
                )
                try {
                    db.collection("users").document(user.uid).set(profile).await()
                } catch (e: Exception) {
                    // Roll back so the user can retry instead of having a login with no profile
                    user.delete().await()
                    throw e
                }

                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = friendlyMessage(e)) }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    private fun validate(
        first: String, last: String, email: String, password: String, confirm: String
    ): String? = when {
        first.isEmpty() || last.isEmpty() -> "Enter your first and last name"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
        password.length < 8 -> "Password must be at least 8 characters"
        password != confirm -> "Passwords don't match"
        else -> null
    }

    private fun friendlyMessage(e: Exception): String = when (e) {
        is FirebaseAuthUserCollisionException -> "An account with that email already exists"
        is FirebaseAuthWeakPasswordException -> "Password is too weak"
        is FirebaseAuthInvalidCredentialsException -> "That email address isn't valid"
        else -> "Couldn't create account. Check your connection and try again"
    }
}