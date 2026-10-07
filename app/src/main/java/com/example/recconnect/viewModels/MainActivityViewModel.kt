package com.example.recconnect.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recconnect.DatabaseRepository
import com.example.recconnect.models.EventData
import com.example.recconnect.Firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel : ViewModel() {
    private val database: DatabaseRepository = Firestore()
    val deviceUserID = Firebase.auth.currentUser?.uid ?: "1" // default to userID: 1

    val events: StateFlow<List<EventData>> = flow {
        emit(database.getAllEvents())
    }
        .catch { e -> Log.e(ContentValues.TAG, "Failed to load events", e) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = emptyList()
        )



}