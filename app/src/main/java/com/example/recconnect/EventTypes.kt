package com.example.recconnect

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsRugby
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.ui.graphics.vector.ImageVector

data class EventType(val key: String, val icon: ImageVector)

object EventTypes {
    val type = listOf(
        EventType("Gym Session", Icons.Filled.FitnessCenter),
        EventType("Run", Icons.AutoMirrored.Filled.DirectionsRun),
        EventType("Basketball", Icons.Filled.SportsBasketball),
        EventType("Football", Icons.Filled.SportsSoccer),
        EventType("Cycling", Icons.AutoMirrored.Filled.DirectionsBike),
        EventType("Rugby", Icons.Filled.SportsRugby),
        EventType("Tennis", Icons.Filled.SportsTennis),
        EventType("Volleyball", Icons.Filled.SportsVolleyball),
        EventType("Netball", Icons.Filled.SportsBasketball),
        EventType("Walk", Icons.AutoMirrored.Filled.DirectionsWalk),
        EventType("Swimming", Icons.Filled.Pool),
        EventType("Pickleball", Icons.Filled.SportsTennis),
        EventType("Hiking", Icons.Filled.Hiking),
        EventType("Other", Icons.Filled.Event)
    )

    // icon for the event card image
    fun iconFor(key: String): ImageVector {
        for (t in type) {
            if (t.key.equals(key, ignoreCase = true)) return t.icon
        }
        return Icons.Filled.Event
    }
}

val skillLevels = listOf("All levels", "Beginner", "Intermediate", "Advanced")