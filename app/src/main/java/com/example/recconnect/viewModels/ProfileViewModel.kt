package com.example.recconnect.viewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.recconnect.models.EventData
import com.example.recconnect.models.UserData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "RecConnect.ProfileViewModel"
class ProfileViewModel : androidx.lifecycle.ViewModel() {

    private val repository: com.example.recconnect.DatabaseRepository =
        _root_ide_package_.com.example.recconnect.Firestore()

    private val _profile =
        _root_ide_package_.kotlinx.coroutines.flow.MutableStateFlow<UserData?>(
            null
        )
    val profile: StateFlow<UserData?> = _profile

    val events: StateFlow<List<EventData>> = flow {
        val userId = repository.currentUserId
        emit(if (userId == null) emptyList() else repository.getJoinedEventObjects(userId))
    }
        .catch { e -> Log.e(TAG, "Failed to load joined events", e) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = emptyList()
        )

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