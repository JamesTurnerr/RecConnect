package com.example.recconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository: DatabaseRepository = Firestore()

    private val _profile = MutableStateFlow<UserData?>(null)
    val profile: StateFlow<UserData?> = _profile

    fun loadProfile(userHandle: String) {
        viewModelScope.launch {
            _profile.value = repository.getUserById(userHandle)
        }
    }

    /*fun updateProfile(
        email: String,
        name: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateUser(
                    email = email,
                    name = name
                )

                // Update the local profile aswell
                _profile.value = _profile.value?.copy(
                    email = email,
                    name = name
                )

                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }*/
}