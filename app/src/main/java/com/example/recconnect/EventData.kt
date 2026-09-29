package com.example.recconnect

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class EventData(
    val id: Int,
    val title: String,
    val location: String,
    val timestamp: Long,
    val attending: List<Int>, //list of user ID's
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
                    "Christchurch",
                    1637653200000,
                    listOf(1, 2),
                    ""
                ),
                EventData(2,
                    "Biking",
                    "Auckland",
                    1637725200000,
                    listOf(13434, 2333, 3121, 2323232),
                    "Mountain biking"
                ),
                EventData(3,
                    "Bicep day the gym",
                    "Christchurch",
                    1637811600000,
                    listOf(16767, 2222),
                    ""
                ),
                EventData(
                    4,
                    "Hiking",
                    "Christchurch",
                    1637898000000,
                    listOf(111),
                    ""
                )
            )
        }
    }
}