package com.example.recconnect.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recconnect.DatabaseRepository
import com.example.recconnect.models.EventData
import com.example.recconnect.Firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "RecConnect.EventCardViewModel"
class EventCardViewModel(eventID: String): ViewModel() {
    val database : DatabaseRepository = Firestore()

    val eventDetails: StateFlow<EventData?> = flow {
        emit(database.getEventById(eventID))
    }
        .catch { e -> Log.e(TAG, "Failed to load event", e) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )


    fun joinEvent(eventID: String, onDone: () -> Unit) {

        viewModelScope.launch {
            try {
                database.joinEvent(eventID)
                onDone()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to join event", e)
            }
        }
    }

    fun leaveEvent(eventID: String, onDone: () -> Unit) {
        viewModelScope.launch {

            try {
                database.leaveEvent(eventID)
                onDone()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to leave event", e)
            }

        }
    }

    suspend fun getAttendeesById(attendeeList: List<String>): List<String> = coroutineScope {
        attendeeList
            .map { id -> async { database.getUserById(id).name } }
            .awaitAll()
    }

    fun isAttending(event: EventData): Boolean =
        database.currentUserId in event.attendeeIds
}