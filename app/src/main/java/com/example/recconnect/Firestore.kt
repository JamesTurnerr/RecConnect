package com.example.recconnect

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

private const val TAG = "RecConnect.Firestore"
class Firestore : DatabaseRepository {
    override val currentUserId: String?
        get() = Firebase.auth.currentUser?.uid
    val db = Firebase.firestore
    private val uid: String
        get() = currentUserId ?: throw IllegalStateException("User not logged in")

    private fun DocumentSnapshot.toEventData(): EventData = EventData(
        id = id,
        title = getString("title") ?: "",
        type = getString("type") ?: "",
        location = getString("location") ?: "",
        timestamp = getLong("timestamp") ?: 11111111111L,
        description = getString("description") ?: "",
        creatorUser = getString("creatorUser") ?: "",
        skillLevel = getString("skillLevel") ?: "",
        maxSpots = getLong("maxSpots")?.toInt() ?: 0,
        attendeeIds = (get("attendeeIds") as? List<*>)
            ?.filterIsInstance<DocumentReference>()
            ?.map { it.id }
            ?: emptyList()
    )

    private fun DocumentSnapshot.getReferenceIds(field: String): List<String> =
        (get(field) as? List<*>)
            ?.filterIsInstance<DocumentReference>()
            ?.map { it.id }
            ?: emptyList()

    private fun DocumentSnapshot.toUserData(): UserData = UserData(
        id = id,
        name = getString("name") ?: "",
        email = getString("email") ?: "",
        events = getReferenceIds("events")
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

    override suspend fun changeAccountDetails(newName: String?, newEmail: String?) {
        val newDetails = mutableMapOf<String, Any>()
        if(!newName.isNullOrBlank())
        {
            newDetails["name"] = newName
        }
        if(!newEmail.isNullOrBlank())
        {
            newDetails["email"] = newEmail
        }
        if(newDetails.isEmpty())
        {
            Log.w(TAG, "Error new details not entered")
            return
        }
        db.collection("users").document(uid).update(newDetails).await()
        Log.d(TAG, "User details updated to $newDetails")


    }

    override suspend fun getJoinedEventIDs(userID: String): List<String> {
        return getUserById(userID).events
    }

    override suspend fun getJoinedEventObjects(userID: String): List<EventData> {
        val userRef = db.collection("users").document(userID)
        return db.collection("events")
            .whereArrayContains("attendeeIds", userRef)
            .get()
            .await()
            .documents
            .map { it.toEventData() }
            .sortedBy { it.timestamp }
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
                                     maxSpots: Int
    ) {
        val event = hashMapOf(
            "title" to title,
            "type" to type,
            "location" to location,
            "timestamp" to timestamp,
            "description" to description,
            "creatorId" to uid,
            "creatorName" to getUserById(uid).name,
            "skillLevel" to skillLevel,
            "maxSpots" to maxSpots,
            "attendeeIds" to listOf<DocumentReference>(db.collection("users").document(uid))
        )
        val userReference = db.collection("users").document(uid)
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

    override suspend fun joinEvent(eventID: String) {
        val userReference = db.collection("users").document(uid)
        val eventReference = db.collection("events").document(eventID)

        db.batch()
            .update(eventReference, "attendeeIds", FieldValue.arrayUnion(userReference))
            .update(userReference, "events", FieldValue.arrayUnion(eventReference))
            .commit()
            .await()
        Log.d(TAG, "User added to event: $eventID")
    }

    override suspend fun leaveEvent(eventID: String) {
        val userReference = db.collection("users").document(uid)
        val eventReference = db.collection("events").document(eventID)

        db.batch()
            .update(eventReference, "attendeeIds", FieldValue.arrayRemove(userReference))
            .update(userReference, "events", FieldValue.arrayRemove(eventReference))
            .commit()
            .await()
        Log.d(TAG, "User removed from event: $eventID")
    }

    override suspend fun getUserById(userID: String): UserData {
        val user = db.collection("users").document(userID).get().await()
        if (user.exists())
        {
            return user.toUserData()
        }
        else
        {
            throw NoSuchElementException("$userID user not found")
        }

    }

    override suspend fun getEventById(eventID: String): EventData {
        val event = db.collection("events")
            .document(eventID).get().await().toEventData()
        return event
    }
}