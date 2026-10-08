package com.example.recconnect

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import com.example.recconnect.models.EventData

class EventViewModel : ViewModel() {
    private val repository: DatabaseRepository = Firestore()
    private val db = Firebase.firestore
    private var listenerRegistration: ListenerRegistration? = null
    private val attendeeCounts = mutableMapOf<String, Int>()

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
        description: String, skillLevel: String, maxSpots: Int
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

    fun checkTodayReminders(context: android.content.Context) {
        viewModelScope.launch {
            try {
                val joined = repository.getJoinedEventObjects(currentUserId)
                val todayCalendar = java.util.Calendar.getInstance()
                val todayYear = todayCalendar.get(java.util.Calendar.YEAR)
                val todayDay = todayCalendar.get(java.util.Calendar.DAY_OF_YEAR)

                for (event in joined) {
                    val eventCalendar = java.util.Calendar.getInstance().apply { timeInMillis = event.timestamp }
                    val eventYear = eventCalendar.get(java.util.Calendar.YEAR)
                    val eventDay = eventCalendar.get(java.util.Calendar.DAY_OF_YEAR)

                    if (eventYear == todayYear && eventDay == todayDay) {
                        NotificationHelper.showNotification(
                            context,
                            "Activity Reminder",
                            "Reminder: '${event.title}' is happening today!"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("EventViewModel", "Failed to check reminders", e)
            }
        }
    }

    fun observeMyCreatedEvents(context: android.content.Context) {
        if (currentUserId.isBlank()) return
        listenerRegistration?.remove()
        try {
            listenerRegistration = db.collection("events")
                .whereEqualTo("creatorId", currentUserId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null || snapshot == null) return@addSnapshotListener
                    for (doc in snapshot.documents) {
                        val eventId = doc.id
                        val title = doc.getString("title") ?: "Your Event"
                        val attendees = (doc.get("attendeeIds") as? List<*>)
                            ?.filterIsInstance<DocumentReference>()
                            ?.map { it.id } ?: emptyList()
                        val count = attendees.size
                        val prevCount = attendeeCounts[eventId] ?: count
                        if (count > prevCount && !attendees.contains(currentUserId)) {
                            NotificationHelper.showNotification(
                                context,
                                "New Attendee Joined!",
                                "Someone joined your event '$title'!"
                            )
                        }
                        attendeeCounts[eventId] = count
                    }
                }
        } catch (e: Exception) {
            Log.e("EventViewModel", "Failed to observe created events", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
