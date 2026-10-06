package com.example.recconnect

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val db = Firebase.firestore

    // gets event from firestore and turns it into eventdata
    suspend fun getAllEvents(): List<EventData> {
        val result = db.collection("events").get().await()
        val list = mutableListOf<EventData>()
        for (doc in result.documents) {
            val people = (doc.get("attendeeIds") ?: doc.get("attending")) as? List<String> ?: emptyList()
            list.add(
                EventData(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    type = doc.getString("type") ?: "",
                    location = doc.getString("location") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L,
                    attending = people,
                    description = doc.getString("description") ?: "",
                    creatorId = doc.get("creatorId")?.toString() ?: "",
                    creatorName = doc.getString("creatorName") ?: "",
                    skillLevel = doc.getString("skillLevel") ?: "",
                    maxSpots = doc.get("maxSpots")?.toString() ?: "",
                    attendeeIds = people
                )
            )
        }
        return list
    }

    // event saved to firestore
    suspend fun createEvent(event: EventData) {
        val data = hashMapOf(
            "title" to event.title,
            "type" to event.type,
            "location" to event.location,
            "timestamp" to event.timestamp,
            "description" to event.description,
            "creatorId" to event.creatorId,
            "creatorName" to event.creatorName,
            "skillLevel" to event.skillLevel,
            "maxSpots" to event.maxSpots,
            "attendeeIds" to event.attendeeIds,
            "attending" to event.attendeeIds
        )
        db.collection("events").add(data).await()
    }
}