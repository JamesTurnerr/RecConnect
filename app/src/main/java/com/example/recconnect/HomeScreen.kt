package com.example.recconnect

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// turns timestamp into text
fun timeUntil(timestamp: Long): String {
    val minutes = (timestamp - System.currentTimeMillis()) / 60000
    val hours = minutes / 60
    val days = hours / 24
    return when {
        days >= 2 -> "in $days days"
        days == 1L -> "tomorrow"
        hours == 1L -> "in 1 hour"
        hours >= 2 -> "in $hours hours"
        minutes <= 1 -> "starting now"
        else -> "in $minutes minutes"
    }
}

@Composable
fun HomeScreen(navController: NavController, eventViewModel: EventViewModel) {
    val now = System.currentTimeMillis()
    val weekFromNow = now + 7 * 24 * 60 * 60 * 1000L

    // upcoming events
    val upcoming = eventViewModel.events
        .filter { it.timestamp >= now }
        .sortedBy { it.timestamp }

    // joined events
    val joined = upcoming.filter { eventViewModel.currentUserId in it.attendeeIds }
    val nextUp = joined.firstOrNull()
    val laterJoined = joined.drop(1)

    // events in the next week the user hasn't joined
    val thisWeek = upcoming.filter {
        it.timestamp <= weekFromNow && eventViewModel.currentUserId !in it.attendeeIds
    }

    LaunchedEffect(Unit) {
        eventViewModel.loadUserName()
        eventViewModel.loadEvents()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.navigate("Settings") },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rec_connect_logo_transparent),
                        contentDescription = "RecConnect logo",
                        modifier = Modifier.height(160.dp)
                    )
                }
            }
        }

        item { Text("Next up", style = MaterialTheme.typography.titleMedium) }
        item {
            if (nextUp == null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("You haven't joined any events yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("Event/${nextUp.id}") }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            EventTypes.iconFor(nextUp.type),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                timeUntil(nextUp.timestamp),
                                color = MaterialTheme.colorScheme.onTertiary,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                nextUp.title,
                                color = MaterialTheme.colorScheme.onTertiary,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "${nextUp.readableTime()} · ${nextUp.location}",
                                color = MaterialTheme.colorScheme.onTertiary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { navController.navigate("createEvent") },
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(" Create")
                }
                Button(
                    onClick = { navController.navigate("Events") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(" Browse")
                }
            }
        }

        // rest of the users joined events
        if (laterJoined.isNotEmpty()) {
            item { Text("Your upcoming events", style = MaterialTheme.typography.titleMedium) }
            items(laterJoined) { event ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("Event/${event.id}") }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.tertiary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                EventTypes.iconFor(event.type),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(event.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${event.readableTime()} · ${event.location}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // other events happening this week (scrolls sideways, could change if weird)
        if (thisWeek.isNotEmpty()) {
            item { Text("Happening this week", style = MaterialTheme.typography.titleMedium) }
            item {
                LazyRow(
                    contentPadding = PaddingValues(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(thisWeek) { event ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { navController.navigate("Event/${event.id}") }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(
                                    EventTypes.iconFor(event.type),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    event.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1
                                )
                                Text(
                                    timeUntil(event.timestamp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    event.location,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}