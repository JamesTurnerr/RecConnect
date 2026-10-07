package com.example.recconnect

import android.app.AlertDialog
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    val eventData = loadedEvent
    if (eventData == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
        )
        {
            Image(
                painter = painterResource(id = R.drawable.defaultpfp),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = eventData.title, style = MaterialTheme.typography.headlineMedium, fontSize = 36.sp)
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        //Location
        Row(
            modifier = Modifier
        )
        {
            Image(
                painter = painterResource(id = R.drawable.location_icon),
                contentDescription = "Location Icon",
                modifier = Modifier.size(28.dp).clip(CircleShape)
            )
            Text(
                text = eventData.location,
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        //Timestamp
        Row(
            modifier = Modifier
        )
        {
            Image(
                painter = painterResource(id = R.drawable.clock_icon),
                contentDescription = "Clock Icon",
                modifier = Modifier.size(32.dp).clip(CircleShape)
            )
            Text(
                text = eventData.readableTime(),
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        //Attendees
        val attendeesString = stringResource(R.string.attendees)
        val closeString = stringResource(R.string.close)
        Text(
            text = stringResource(R.string.attending),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
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
                    contentDescription = "Profile Picture",
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

        Spacer(modifier = Modifier.height(10.dp))

        //Description
        if(eventData.description != "")
        {
            Text(
                text = stringResource(R.string.description, eventData.description),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
            )
        }

        /*
        Dialog box when user clicks join/leave event
        Add to event then redirect to home screen on accept
         */
        //leave event
        if(eventData.attendeeIds.contains(eventCardViewModel.deviceUserID))
        {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
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

                                eventCardViewModel.leaveEvent(navController, eventData.id)
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
        //join event
        else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            )
            {
                val confirmationMessage =
                    stringResource(R.string.join_event_confirmation, eventData.title)
                Button(
                    modifier = Modifier.width(200.dp).height(67.dp),
                    onClick = {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage(confirmationMessage)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { dialog, id ->

                                eventCardViewModel.joinEvent(navController, eventData.id)
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
                        text = stringResource(R.string.join_event),
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}