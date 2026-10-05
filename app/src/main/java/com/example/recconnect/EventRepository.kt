package com.example.recconnect

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class EventRepository {
    private val db = Firebase.firestore
    private val eventsCollection = db.collection("events")

    fun getAllEvents(onResult: (List<Event>) -> Unit) {
        eventsCollection.get()
            .addOnSuccessListener { snapshot ->
                val events = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                }
                onResult(events)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun addEvent(event: Event, onComplete: (Boolean) -> Unit) {
        val eventToSave = event.copy(id = "")
        eventsCollection.add(eventToSave)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}