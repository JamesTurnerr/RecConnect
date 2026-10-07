package com.example.recconnect.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.recconnect.DatabaseRepository
import com.example.recconnect.models.EventData
import com.example.recconnect.Firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
        .catch { e -> Log.e(ContentValues.TAG, "Failed to load event", e) }
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

    fun leaveEvent(navController: NavController, eventID: String)
    {

        database.leaveEvent(eventID)

        navController.navigate("Home")
    }

    suspend fun getAttendeesById(attendeeList: List<String>): List<String> = coroutineScope {
        attendeeList
            .map { id -> async { database.getUserById(id).name } }
            .awaitAll()
    }
}