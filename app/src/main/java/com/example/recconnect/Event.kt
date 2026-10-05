package com.example.recconnect

data class Event(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val location: String = "",
    val date: String = "",
    val time: String = "",
    val description: String = "",
    val creatorId: String = "",
    val creatorName: String = "",
    val skillLevel: String = "",
    val maxSpots: Int = 0,
    val attendeeIds: List<String> = emptyList(),
    val imageUrl: String = ""
)

data class EventType(val key: String)

object EventTypes {
    val type = listOf(
        EventType("Gym Session"),
        EventType("Run"),
        EventType("Basketball"),
        EventType("Football"),
        EventType("Cycling"),
        EventType("Rugby"),
        EventType("Tennis"),
        EventType("Volleyball"),
        EventType("Netball"),
        EventType("Walk"),
        EventType("Swimming"),
        EventType("Pickleball"),
        EventType("Hiking"),
        EventType("Other")
    )
}