package com.example.recconnect

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class EventViewModel : ViewModel() {
    val events = mutableStateListOf<Event>()

    fun addEvent(event: Event) {
        events.add(event)
    }
}