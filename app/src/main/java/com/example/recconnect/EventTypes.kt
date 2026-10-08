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
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

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

// with string resources
@Composable
fun typeLabel(key: String): String {
    return when (key) {
        "Gym Session" -> stringResource(R.string.type_gym_session)
        "Run" -> stringResource(R.string.type_run)
        "Basketball" -> stringResource(R.string.type_basketball)
        "Football" -> stringResource(R.string.type_football)
        "Cycling" -> stringResource(R.string.type_cycling)
        "Rugby" -> stringResource(R.string.type_rugby)
        "Tennis" -> stringResource(R.string.type_tennis)
        "Volleyball" -> stringResource(R.string.type_volleyball)
        "Netball" -> stringResource(R.string.type_netball)
        "Walk" -> stringResource(R.string.type_walk)
        "Swimming" -> stringResource(R.string.type_swimming)
        "Pickleball" -> stringResource(R.string.type_pickleball)
        "Hiking" -> stringResource(R.string.type_hiking)
        "Other" -> stringResource(R.string.type_other)
        else -> key
    }
}

@Composable
fun skillLabel(key: String): String {
    return when (key) {
        "All levels" -> stringResource(R.string.skill_all)
        "Beginner" -> stringResource(R.string.skill_beginner)
        "Intermediate" -> stringResource(R.string.skill_intermediate)
        "Advanced" -> stringResource(R.string.skill_advanced)
        else -> key
    }
}