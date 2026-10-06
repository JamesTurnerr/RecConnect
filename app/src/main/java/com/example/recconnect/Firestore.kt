package com.example.recconnect

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import kotlin.String

class Firestore : DatabaseRepository {
    val db = Firebase.firestore
    var userID = "1";//TODO("Set user ID to actual user id")

    override fun createAccount(name: String, email: String): Boolean {
        val events = ArrayList<Int>()
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "events" to events
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        return true//does not yet return true or false on successful account creation
    }

    override fun changeAccountName(newName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getJoinedEvents(): List<String> {//new
        return try {
            val eventNames = db.collection("events")
                .whereArrayContains("attending", userID).get().await()
            eventNames.documents.mapNotNull { it.id }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting events", e)
            emptyList()
        }
    }

    override suspend fun getAllEvents(): List<String> {
        return try {
            val eventNames = db.collection("events").get().await()
            eventNames.documents.mapNotNull { doc ->
                doc.getString("name")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting events", e)
            emptyList()
        }
    }

    override fun createEvent(title: String,
                             type: String,
                             location: String,
                             timestamp: Long,
                             description: String,
                             skillLevel: String,
                             maxSpots: String
    ) {
        val event = hashMapOf(
            "title" to title,
            "type" to type,
            "location" to location,
            "timestamp" to timestamp,
            "description" to description,
            "creatorId" to 1, //TODO: set this value to the ID of the user that creates the event
            "creatorName" to "placeholder creator name", //TODO: set this value to the name of the user that creates the event
            "skillLevel" to skillLevel,
            "maxSpots" to maxSpots,
            "attendeeIds" to listOf("1")//TODO: automatically add the creator user id to this list
        )

        db.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Event created with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error creating event", e)
            }
    }

    override fun joinEvent(eventID: String) {
        db.collection("events")
            .document(eventID)
            .update("attending", FieldValue.arrayUnion(userID))
            .addOnSuccessListener {
                Log.d(TAG, "User added to event: $eventID")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user to event", e)
            }
    }

    override fun leaveEvent(eventID: String) {
        db.collection("events")
            .document(eventID)
            .update("attending", FieldValue.arrayRemove(userID))
            .addOnSuccessListener {
                Log.d(TAG, "User removed from event: $eventID")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error removing user from event", e)
            }
    }

    override suspend fun getUserById(userID: String): UserData {
        val user = db.collection("users")
            .document(userID).get().await()

        return UserData(
            name = user.getString("name") ?: "",
            email = user.getString("email") ?: "",
            events = emptyList()
        )
    }

    override suspend fun getEventById(eventID: String): EventData {
        val event = db.collection("events")
            .document(eventID).get().await()

        return EventData(
            id = eventID,
            title = event.getString("title") ?: "",
            type = event.getString("type") ?: "",
            location = event.getString("location") ?: "",
            timestamp = event.getLong("timestamp") ?: 1637653200000,
            attending = (event.get("attending") ?: "") as List<String>,
            description = event.getString("description") ?: "",
            creatorId = event.getString("creatorId") ?: "",
            creatorName = event.getString("creatorName") ?: "",
            skillLevel = event.getString("skillLevel") ?: "",
            maxSpots = event.getString("maxSpots") ?: "",
            attendeeIds = (event.get("attendeeIds") ?: "") as List<String>
        )
    }
}