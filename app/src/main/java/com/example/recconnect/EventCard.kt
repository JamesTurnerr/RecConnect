package com.example.recconnect

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.recconnect.viewModels.EventCardViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun EventCard(navController: NavController, eventId: String) {
    val eventCardViewModel: EventCardViewModel = viewModel(key = eventId) {
        EventCardViewModel(
            eventId
        )
    }
    val loadedEvent by eventCardViewModel.eventDetails.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val creatorId = loadedEvent?.attendeeIds?.firstOrNull() ?: ""
    val creatorName by produceState("", creatorId) {
        if (creatorId.isNotBlank()) {
            value = try {
                eventCardViewModel.getAttendeesById(listOf(creatorId)).firstOrNull() ?: ""
            } catch (e: Exception) {
                ""
            }
        }
    }

    val eventData = loadedEvent
    if (eventData == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // back button and event type image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription =stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Icon(
                EventTypes.iconFor(eventData.type),
                contentDescription = eventData.type,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(56.dp).align(Alignment.Center)
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = creatorId.isNotBlank()) {
                            navController.navigate("Profile/$creatorId")
                        }
                        .padding(4.dp)
                )
                {
                    // pfp
                    Image(
                        painter = painterResource(id = R.drawable.defaultpfp),
                        contentDescription = stringResource(R.string.event_profile_picture),
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // creator name
                    Text(
                        text = if (creatorName.isNotBlank()) stringResource(R.string.event_by, creatorName) else "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // title
                Text(text = eventData.title, style = MaterialTheme.typography.titleLarge)

                // event type and skill level
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(eventData.type, color = MaterialTheme.colorScheme.onSecondary)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(eventData.skillLevel, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))

                // location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val uri = Uri.parse("geo:0,0?q=" + Uri.encode(eventData.location))
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = stringResource(R.string.event_location), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = eventData.location, style = MaterialTheme.typography.bodyLarge)
                }

                // timestamp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                )
                {
                    Icon(Icons.Filled.Schedule, contentDescription = stringResource(R.string.event_time), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = eventData.readableTime(), style = MaterialTheme.typography.bodyLarge)
                }

                // max spots
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Group, contentDescription = stringResource(R.string.event_spots_icon), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.event_spots_filled, eventData.attendeeIds.size, eventData.maxSpots),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))

                // attendees
                val attendeesString = stringResource(R.string.attendees)
                val closeString = stringResource(R.string.close)
                Text(
                    text = stringResource(R.string.attending),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-14).dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = eventData.attendeeIds.isNotEmpty()) {
                            scope.launch {
                                val names = eventCardViewModel.getAttendeesById(eventData.attendeeIds)

                                AlertDialog.Builder(context)
                                    .setTitle(attendeesString)
                                    .setItems(names.toTypedArray()) { _, which ->
                                        val userId = eventData.attendeeIds[which]
                                        navController.navigate("Profile/$userId")
                                    }
                                    .setPositiveButton(closeString) { dialog, _ -> dialog.dismiss() }
                                    .show()
                            }
                        }
                        .padding(4.dp)
                )
                {
                    for(attendee in eventData.attendeeIds.take(3))
                    {
                        Image(
                            painter = painterResource(id = R.drawable.defaultpfp),
                            contentDescription = stringResource(R.string.event_profile_picture),
                            modifier = Modifier.size(28.dp).clip(CircleShape)
                        )
                    }

                    if(eventData.attendeeIds.size > 3)
                    {
                        Spacer(Modifier.width(30.dp))
                        Text(
                            text = pluralStringResource(R.plurals.others_attending, eventData.attendeeIds.size - 3, eventData.attendeeIds.size - 3),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
                        )
                    }
                }

                // description
                if(eventData.description != "") {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                    Text(
                        text = stringResource(R.string.description, eventData.description),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        /*
        Dialog box when user clicks join/leave event
        Add to event then redirect to home screen on accept
         */
        // leave event
        if (eventCardViewModel.isAttending(eventData))
        {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            )
            {
                val confirmationMessage =
                    stringResource(R.string.leave_event_confirmation, eventData.title)
                Button(
                    modifier = Modifier.width(200.dp).height(67.dp),
                    onClick = {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage(confirmationMessage)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { dialog, id ->

                                eventCardViewModel.leaveEvent(eventData.id) {navController.popBackStack()}
                            }
                            .setNegativeButton(R.string.cancel) { dialog, id ->
                                dialog.dismiss() // Close the dialog
                            }
                        val alert = builder.create()
                        alert.show()
                    }
                )
                {
                    Text(
                        text = stringResource(R.string.leave_event),
                        fontSize = 24.sp
                    )
                }
            }
        }
        // join event
        else {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            )
            {
                val confirmationMessage =
                    stringResource(R.string.join_event_confirmation, eventData.title)
                Button(
                    enabled = eventData.attendeeIds.size < eventData.maxSpots,
                    modifier = Modifier.width(200.dp).height(67.dp),
                    onClick = {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage(confirmationMessage)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { dialog, id ->

                                eventCardViewModel.joinEvent(eventData.id){ navController.popBackStack() }
                            }
                            .setNegativeButton(R.string.cancel) { dialog, id ->
                                dialog.dismiss() // Close the dialog
                            }
                        val alert = builder.create()
                        alert.show()
                    }
                )
                {
                    Text(
                        text = stringResource(
                            if (eventData.attendeeIds.size < eventData.maxSpots) R.string.join_event
                            else R.string.event_full
                        ),
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}