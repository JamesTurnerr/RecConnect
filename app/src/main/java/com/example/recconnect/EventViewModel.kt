package com.example.recconnect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val repository = EventRepository()

    // placeholder users - replace after merge login!!
    val currentUserId = "1"
    val currentUserName = "Test User"

    var events by mutableStateOf<List<EventData>>(emptyList())
        private set

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            try {
                events = repository.getAllEvents()
            } catch (e: Exception) {
            }
        }
    }

    fun createEvent(
        title: String, type: String, location: String, timestamp: Long,
        description: String, skillLevel: String, maxSpots: String
    ) {
        val newEvent = EventData(
            id = "",
            title = title,
            type = type,
            location = location,
            timestamp = timestamp,
            attending = listOf(currentUserId),
            description = description,
            creatorId = currentUserId,
            creatorName = currentUserName,
            skillLevel = skillLevel,
            maxSpots = maxSpots,
            attendeeIds = listOf(currentUserId)
        )
        viewModelScope.launch {
            try {
                repository.createEvent(newEvent)
                loadEvents()
            } catch (e: Exception) {
            }
        }
    }
}