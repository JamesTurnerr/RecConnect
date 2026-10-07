package com.example.recconnect

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class DatabaseTestViewModel: ViewModel() {
    val database : DatabaseRepository = Firestore()
    var deviceUserID = Firebase.auth.currentUser?.uid ?: "1"//default to userID: 1
    var name by mutableStateOf("")
        private set

    fun updateName(newName: String) {
        name = newName
    }

    var eventIDinput by mutableStateOf("")
        private set

    fun updateEventIDinput(newEventIDinput: String) {
        eventIDinput = newEventIDinput
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

    fun loadEvents() {
        viewModelScope.launch {
            val allEvents = database.getAllEvents()
            for(event in allEvents)
            {
                Log.d(TAG, "Event ID: ${event.id}")
                Log.d(TAG, "Event title: ${event.title}")
            }
        }
    }
    fun addUserToEvent()
    {
        database.joinEvent(eventIDinput)//existing event id
    }
    fun outputUserData() {
        viewModelScope.launch {
            try {
                val user = database.getUserById(deviceUserID)
                Log.d(TAG, "User email: ${user.email}")
                Log.d(TAG, "User name: ${user.name}")
            } catch (e: Exception) {
                Log.e(TAG, "User not found", e)
            }
        }
    }
    fun outputEventData() {
        viewModelScope.launch {
            try {
                val event = database.getEventById("1")
                Log.d(TAG, "Event id: ${event.id}")
                Log.d(TAG, "Event title: ${event.title}")
                Log.d(TAG, "Event type: ${event.type}")
                Log.d(TAG, "Event location: ${event.location}")
                for(a in event.attendeeIds)
                {
                    Log.d(TAG, "Attending: $a")
                }
                Log.d(TAG, "Timestamp: ${event.readableTime()}")
            } catch (e: Exception) {
                Log.e(TAG, "Event not found", e)
            }
        }
    }
    fun createNewAccount()
    {
        database.createAccount(name, email)
    }
    fun createNewEvent()
    {
        viewModelScope.launch {
            database.createEvent(
                eventName,
                "EventType",
                "Location",
                1111111111,
                "A description of the event",
                "Event skill level",
                "10"
            )//placeholder timestamp
        }
    }
    fun changeAccountName(userID: String, newName: String)
    {
        database.changeAccountDetails(newName = newName)
    }
    fun getEventWithID(eventID:String)
    {
        viewModelScope.launch {
            try {
                val event = database.getEventById(eventID)
                Log.d(TAG, "id ${event.id}")
                Log.d(TAG, "title ${event.title}")
                Log.d(TAG, "type ${event.type}")
                for(atendee in event.attendeeIds)
                {
                    Log.d(TAG, "attendee $atendee")
                }
                Log.d(TAG, "creator id ${event.creatorUser}")

            } catch (e: Exception) {
                Log.e(TAG, "event not found", e)
            }
        }
    }

    fun getJoinedEventIDs(userID: String)
    {
        viewModelScope.launch {
            try {
                val events = database.getJoinedEventIDs(userID)

                for(event in events)
                {
                    Log.d(TAG, "eventid: $event")
                }

            } catch (e: Exception) {
                Log.e(TAG, "events not found", e)
            }
        }
    }

}