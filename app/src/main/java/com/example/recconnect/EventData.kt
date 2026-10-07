package com.example.recconnect

import com.google.firebase.firestore.DocumentId
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class EventData(
    @DocumentId val id: String,
    val title: String,
    val type: String,
    val location: String,
    val timestamp: Long,
    val description: String,
    val creatorUser: String,
    val skillLevel: String,
    val maxSpots: String,
    val attendeeIds: List<String>
) {
    /**
     * Convert a timestamp to a human readable form
     */
    fun readableTime(): String {
        val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.timestamp
        return dateFormat.format(calendar.time)
    }

    companion object {
        /**
         * Gets a list of basic (pre-defined) events
         */
        fun getEvents(): List<EventData> {
            return listOf(
                EventData(
                    "1",
                    "Running",
                    "Run",
                    "Christchurch",
                    1637653200000,
                    "",
                    "1",
                    "Easy",
                    "10",
                    listOf("1", "2")
                ),
                EventData("2",
                    "Biking",
                    "Cycling",
                    "Auckland",
                    1637725200000,
                    "Mountain biking",
                    "1",
                    "Easy",
                    "10",
                    listOf("1", "2")
                ),
                EventData("3",
                    "Bicep day the gym",
                    "Gym",
                    "Christchurch",
                    1637811600000,
                    "",
                    "1",
                    "Easy",
                    "10",
                    listOf("1", "2")
                ),
                EventData(
                    "4",
                    "Hiking",
                    "Hiking",
                    "Christchurch",
                    1637898000000,
                    "",
                    "1",
                    "Easy",
                    "10",
                    listOf("1", "2")
                )
            )
        }
    }
}