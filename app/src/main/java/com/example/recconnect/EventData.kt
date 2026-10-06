package com.example.recconnect

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class EventData(
    val id: String,
    val title: String,
    val type: String,
    val location: String,
    val timestamp: Long,
    val attending: List<String>,
    val description: String,
    val creatorId: String,
    val creatorName: String,
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
                    listOf("1", "2"),
                    "",
                    "1",
                    "Bob",
                    "Easy",
                    "10",
                    listOf("1", "2")
                ),
                EventData("2",
                    "Biking",
                    "Cycling",
                    "Auckland",
                    1637725200000,
                    listOf("13434", "2333", "3121", "2323232"),
                    "Mountain biking",
                    "1",
                    "Bob",
                    "Easy",
                    "10",
                    listOf("1", "2")
                ),
                EventData("3",
                    "Bicep day the gym",
                    "Gym",
                    "Christchurch",
                    1637811600000,
                    listOf("16767", "2222"),
                    "",
                    "1",
                    "Bob",
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
                    listOf("111"),
                    "",
                    "1",
                    "Bob",
                    "Easy",
                    "10",
                    listOf("1", "2")
                )
            )
        }
    }
}