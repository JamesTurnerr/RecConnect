package com.example.recconnect.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recconnect.DatabaseRepository
import com.example.recconnect.Firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


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
                Log.d(ContentValues.TAG, "Event ID: ${event.id}")
                Log.d(ContentValues.TAG, "Event title: ${event.title}")
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
                Log.d(ContentValues.TAG, "User email: ${user.email}")
                Log.d(ContentValues.TAG, "User name: ${user.name}")
            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "User not found", e)
            }
        }
    }
    fun outputEventData() {
        viewModelScope.launch {
            try {
                val event = database.getEventById("1")
                Log.d(ContentValues.TAG, "Event id: ${event.id}")
                Log.d(ContentValues.TAG, "Event title: ${event.title}")
                Log.d(ContentValues.TAG, "Event type: ${event.type}")
                Log.d(ContentValues.TAG, "Event location: ${event.location}")
                for(a in event.attendeeIds)
                {
                    Log.d(ContentValues.TAG, "Attending: $a")
                }
                Log.d(ContentValues.TAG, "Timestamp: ${event.readableTime()}")
            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "Event not found", e)
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
                Log.d(ContentValues.TAG, "id ${event.id}")
                Log.d(ContentValues.TAG, "title ${event.title}")
                Log.d(ContentValues.TAG, "type ${event.type}")
                for(atendee in event.attendeeIds)
                {
                    Log.d(ContentValues.TAG, "attendee $atendee")
                }
                Log.d(ContentValues.TAG, "creator id ${event.creatorUser}")

            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "event not found", e)
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
                    Log.d(ContentValues.TAG, "eventid: $event")
                }

            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "events not found", e)
            }
        }
    }

}