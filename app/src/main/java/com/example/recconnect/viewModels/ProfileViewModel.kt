package com.example.recconnect.viewModels

import androidx.lifecycle.viewModelScope
import com.example.recconnect.models.UserData
import kotlinx.coroutines.launch

class ProfileViewModel : androidx.lifecycle.ViewModel() {

    private val repository: com.example.recconnect.DatabaseRepository =
        _root_ide_package_.com.example.recconnect.Firestore()

    private val _profile =
        _root_ide_package_.kotlinx.coroutines.flow.MutableStateFlow<UserData?>(
            null
        )
    val profile: kotlinx.coroutines.flow.StateFlow<UserData?> = _profile

    fun loadProfile(userHandle: String) {
        viewModelScope.launch {
            _profile.value = repository.getUserById(userHandle)
        }
    }

    fun updateProfile(
        email: String,
        name: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.changeAccountDetails(
                    newName = name,
                    newEmail = email
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
    }
}