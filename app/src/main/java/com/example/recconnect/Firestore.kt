package com.example.recconnect

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

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

    override fun createEvent(name: String, location: String, timestamp: Long) {
        val attending = ArrayList<Int>()
        val event = hashMapOf(
            "name" to name,
            "location" to location,
            "timestamp" to timestamp,
            "attending" to attending

        )

        db.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
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
}