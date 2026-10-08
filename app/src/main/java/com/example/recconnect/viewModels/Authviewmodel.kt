package com.example.recconnect.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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
    val error: String? = null,       // form-level error, shown in a banner
    val emailError: String? = null,  // server-side email problem, shown under the email field
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

        // Safety net: the screen already blocks invalid input before calling this
        val problems = listOfNotNull(
            Validators.name(first, "first name"),
            Validators.name(last, "last name"),
            Validators.email(mail),
            Validators.password(password),
            Validators.confirmPassword(password, confirmPassword)
        )
        if (problems.isNotEmpty()) {
            _uiState.update { it.copy(error = problems.first()) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, emailError = null) }
            try {
                // 1. Create the login (Firebase hashes/stores the password)
                val result = auth.createUserWithEmailAndPassword(mail, password).await()
                val user = result.user ?: throw IllegalStateException("No user returned")

                // 2. Create the profile doc in the "users" collection, using the auth uid as the id
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
                _uiState.update {
                    when (e) {
                        is FirebaseAuthUserCollisionException -> it.copy(
                            isLoading = false,
                            emailError = "An account with this email already exists. Try signing in"
                        )
                        is FirebaseAuthInvalidCredentialsException -> it.copy(
                            isLoading = false,
                            emailError = "Enter a valid email, e.g. name@example.com"
                        )
                        is FirebaseAuthWeakPasswordException -> it.copy(
                            isLoading = false,
                            error = "That password is too weak. Try a longer or less common one"
                        )
                        else -> it.copy(
                            isLoading = false,
                            error = "Couldn't create account. Check your connection and try again"
                        )
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        val mail = email.trim().lowercase()

        if (mail.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(error = "Enter your email and password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, emailError = null) }
            try {
                auth.signInWithEmailAndPassword(mail, password).await()
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: Exception) {
                val msg = when (e) {
                    // Same message for wrong email and wrong password, so it doesn't reveal which accounts exist
                    is FirebaseAuthInvalidUserException,
                    is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password"
                    else -> "Couldn't sign in. Check your connection and try again"
                }
                _uiState.update { it.copy(isLoading = false, error = msg) }
            }
        }
    }

    // Call this from the Profile screen's logout button
    fun logout() {
        auth.signOut()
        _uiState.update { it.copy(isLoggedIn = false) }
    }

    fun clearError() = _uiState.update { it.copy(error = null, emailError = null) }
}