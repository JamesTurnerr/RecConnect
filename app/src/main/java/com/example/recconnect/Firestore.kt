package com.example.recconnect

import android.content.ContentValues.TAG
import android.util.Log
import com.example.recconnect.models.EventData
import com.example.recconnect.models.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import kotlin.String

class Firestore : DatabaseRepository {
    val db = Firebase.firestore
    var deviceUserID = Firebase.auth.currentUser?.uid ?: "1"//default to userID: 1

    private fun DocumentSnapshot.toEventData(): EventData = EventData(
        id = id,
        title = getString("title") ?: "",
        type = getString("type") ?: "",
        location = getString("location") ?: "",
        timestamp = getLong("timestamp") ?: 11111111111L,
        description = getString("description") ?: "",
        creatorUser = getString("creatorUser") ?: "",
        skillLevel = getString("skillLevel") ?: "",
        maxSpots = getString("maxSpots") ?: "",
        attendeeIds = (get("attendeeIds") as? List<*>)
            ?.filterIsInstance<DocumentReference>()
            ?.map { it.id }
            ?: emptyList()
    )

    override fun createAccount(name: String, email: String): Boolean {
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "events" to emptyList<DocumentReference>()
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "User added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user", e)
            }
        return true//TODO: does not yet return true or false on successful account creation
    }

    override fun changeAccountDetails(newName: String?, newEmail: String?) {
        val newDetails = mutableMapOf<String, Any>()
        if(!newName.isNullOrBlank())
        {
            newDetails["name"] = newName
        }
        if(!newEmail.isNullOrBlank())
        {
            newDetails["email"] = newEmail
        }
        if(!newDetails.isEmpty())
        {
            db.collection("users").document(deviceUserID)
                .update(newDetails)
                .addOnSuccessListener { Log.d(TAG, "Users details updated to $newDetails") }
                .addOnFailureListener { e -> Log.w(TAG, "Users details update failed", e) }
        }

    }

    override suspend fun getJoinedEventIDs(userID: String): List<String> {
        return try {
            val eventIDs = db.collection("users").document(userID)
                .get().await()
            (eventIDs.get("events") as? List<DocumentReference>)?.map {it.id} ?: emptyList()
        } catch (e: Exception) {
            Log.w(TAG, "Error getting IDs", e)
            emptyList()
        }
    }

    override suspend fun getAllEvents(): List<EventData> {
        return try {
            val allEvents = db.collection("events").get().await()
            allEvents.documents.map { it.toEventData() }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting events", e)
            emptyList()
        }
    }

    override suspend fun createEvent(title: String,
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
            "creatorId" to deviceUserID,
            "creatorName" to getUserById(deviceUserID).name,
            "skillLevel" to skillLevel,
            "maxSpots" to maxSpots,
            "attendeeIds" to listOf<DocumentReference>(db.collection("users").document(deviceUserID))
        )
        val userReference = db.collection("users").document(deviceUserID)
        val eventReference = db.collection("events").document()
        try {
            db.batch().set(eventReference, event)
                .update(userReference, "events", FieldValue.arrayUnion(eventReference)).commit().await()
            Log.d(TAG, "Event created with ID: ${eventReference.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Error creating event", e)
            throw e // or return a Result / false so the UI can react
        }

    }

    override fun joinEvent(eventID: String) {
        //Add user to event attending list
        db.collection("events")
            .document(eventID)
            .update("attendeeIds", FieldValue.arrayUnion(db.collection("users").document(deviceUserID)))
            .addOnSuccessListener {
                Log.d(TAG, "User added to event: $eventID")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user to event", e)
            }
        //Add event to users events list
        db.collection("users")
            .document(deviceUserID)
            .update("events", FieldValue.arrayUnion(db.collection("events").document(eventID)))
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
            .update("attendeeIds", FieldValue.arrayRemove(db.collection("users").document(deviceUserID)))
            .addOnSuccessListener {
                Log.d(TAG, "User removed from event: $eventID")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error removing user from event", e)
            }
        db.collection("users")
            .document(deviceUserID)
            .update("events", FieldValue.arrayRemove(db.collection("events").document(eventID)))
            .addOnSuccessListener {
                Log.d(TAG, "User removed to event: $eventID")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error removing user to event", e)
            }
    }

    override suspend fun getUserById(userID: String): UserData {
        val user = db.collection("users")
            .document(userID).get().await()

        return UserData(
            id = userID,
            name = user.getString("name") ?: "",
            email = user.getString("email") ?: "",
            events = emptyList()
        )
    }

    override suspend fun getEventById(eventID: String): EventData {
        val event = db.collection("events")
            .document(eventID).get().await().toEventData()
        return event
    }
}