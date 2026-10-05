package com.example.recconnect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class EventViewModel : ViewModel() {
    private val repository = EventRepository()

    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    init {
        refreshEvents()
    }

    fun refreshEvents() {
        repository.getAllEvents { fetchedEvents ->
            events = fetchedEvents
        }
    }

    fun addEvent(event: Event, onComplete: (Boolean) -> Unit = {}) {
        repository.addEvent(event) { success ->
            if (success) refreshEvents()
            onComplete(success)
        }
    }
}