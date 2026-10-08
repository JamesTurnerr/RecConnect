package com.example.recconnect

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val repository: DatabaseRepository = Firestore()

    val currentUserId: String get() = Firebase.auth.currentUser?.uid ?: ""

    var currentUserName by mutableStateOf("")
        private set

    var events by mutableStateOf<List<EventData>>(emptyList())
        private set

    var errorMessage by mutableStateOf("")
        private set

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            try {
                events = repository.getAllEvents()
                errorMessage = ""
            } catch (e: Exception) {
                Log.e("EventViewModel", "load failed", e)
                errorMessage = "Load failed: ${e.message}"
            }
        }
    }

    fun loadUserName() {
        viewModelScope.launch {
            try {
                currentUserName = repository.getUserById(currentUserId).name
            } catch (e: Exception) {
                Log.e("EventViewModel", "name load failed", e)
            }
        }
    }

    fun createEvent(
        title: String, type: String, location: String, timestamp: Long,
        description: String, skillLevel: String, maxSpots: String
    ) {
        viewModelScope.launch {
            try {
                repository.createEvent(title, type, location, timestamp, description, skillLevel, maxSpots)
                loadEvents()
            } catch (e: Exception) {
                Log.e("EventViewModel", "save failed", e)
                errorMessage = "Save failed: ${e.message}"
            }
        }
    }
}