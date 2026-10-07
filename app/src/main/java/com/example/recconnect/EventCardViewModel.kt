package com.example.recconnect

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class EventCardViewModel(eventID: String): ViewModel() {
    val database : DatabaseRepository = Firestore()
    var deviceUserID = Firebase.auth.currentUser?.uid ?: "1"//default to userID: 1

    val eventDetails: StateFlow<EventData?> = flow {
        emit(database.getEventById(eventID))
    }
        .catch { e -> Log.e(TAG, "Failed to load event", e) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )


    fun joinEvent(navController: NavController, eventID: String)
    {

        database.joinEvent(eventID)

        navController.navigate("Home")
    }
}