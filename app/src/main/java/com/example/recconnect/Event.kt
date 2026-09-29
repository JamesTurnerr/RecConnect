package com.example.recconnect

import android.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Event(navController: NavController, eventId: String) {
    val eventData: EventData = EventData.getEvents().first { eventData -> eventData.id == eventId.toInt() }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Event Title: ${eventData.title}", style = MaterialTheme.typography.headlineMedium)
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        Text(
            text = "Location: ${eventData.location} Time: ${eventData.readableTime()}",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Attending: ${eventData.attending}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
        )
        Text(
            text = "Additional Details: ${eventData.additionalDetails}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
        )

        /*
        Dialog box when user clicks join event
        Add to event then redirect to home screen on accept
         */
        Button(onClick = {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to join ${eventData.title}?")
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, id -> joinEvent(navController, eventData.id) }
                .setNegativeButton("Cancel") { dialog, id -> dialog.dismiss() // Close the dialog
                }
            val alert = builder.create()
            alert.show()
        }) {
            Text(text = "Join Event")
        }
    }
}

fun joinEvent(navController: NavController, eventID: Int)
{
    //TODO: add user to event
    //...


    navController.navigate("Home")
}