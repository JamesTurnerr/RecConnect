package com.example.recconnect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DatabaseTestViewModel: ViewModel() {
    val database : DatabaseRepository = Firestore()
    var name by mutableStateOf("")
        private set

    fun updateName(newName: String) {
        name = newName
    }

    var email by mutableStateOf("")
        private set

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    var eventName by mutableStateOf("")
        private set

    fun updateEventName(newEventName: String) {
        eventName = newEventName
    }

    var allEvents by mutableStateOf<List<String>>(emptyList())
        private set
    fun loadEvents() {
        viewModelScope.launch {
            allEvents = database.getJoinedEvents()
        }
    }
    fun addUserToEvent()
    {
        database.joinEvent("knPFKfHUS0jg54XHpXyt")
    }
    fun createNewAccount()
    {
        database.createAccount(name, email)
    }
    fun createNewEvent()
    {
        database.createEvent(eventName, "Location", 1111111111)//placeholder timestamp
    }

}