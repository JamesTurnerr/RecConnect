package com.example.recconnect

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class EventData(
    val id: Int,
    val title: String,
    val location: String,
    val timestamp: Long,
    val attending: String, // should eventually change this type from string to profile object
    val additionalDetails: String
) {
    /**
     * Convert a timestamp to a human readable form
     */
    fun readableTime(): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
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
                    1,
                    "Running",
                    "Chch",
                    1637653200000,
                    "Bob",
                    ""
                ),
                EventData(2,
                    "Biking",
                    "Chch",
                    1637725200000,
                    "Bob, Dave",
                    "Mountain biking"
                ),
                EventData(3,
                    "Bicep day the gym",
                    "Chch",
                    1637811600000,
                    "Dave, Steve",
                    ""
                ),
                EventData(
                    4,
                    "Hiking",
                    "Chch",
                    1637898000000,
                    "Steve",
                    ""
                )
            )
        }
    }
}