package com.example.recconnect.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recconnect.DatabaseRepository
import com.example.recconnect.Firestore
import com.example.recconnect.models.EventData
import com.example.recconnect.models.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "RecConnect.ProfileViewModel"
class ProfileViewModel : ViewModel() {

    private val repository: DatabaseRepository = Firestore()

    private val _profile =
        MutableStateFlow<UserData?>(null)

    private val _events = MutableStateFlow<List<EventData>>(emptyList())
    val events: StateFlow<List<EventData>> = _events.asStateFlow()
    val profile: StateFlow<UserData?> = _profile

    fun loadProfile(userHandle: String) {
        _profile.value = null
        _events.value = emptyList()
        viewModelScope.launch {
            try {
                _profile.value = repository.getUserById(userHandle)
                _events.value = repository.getJoinedEventObjects(userHandle)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load profile for $userHandle", e)
            }
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