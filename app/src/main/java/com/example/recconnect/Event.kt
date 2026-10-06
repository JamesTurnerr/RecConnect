package com.example.recconnect

import android.app.AlertDialog
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Event(navController: NavController, eventId: String) {
    val eventData: EventData = EventData.getEvents().first { eventData -> eventData.id == eventId }
    val context = LocalContext.current
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
            Text(text = eventData.title, style = MaterialTheme.typography.headlineMedium)
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        Row(
            modifier = Modifier
        )
        {
            Image(
                painter = painterResource(id = R.drawable.location_icon),
                contentDescription = "Location Icon",
                modifier = Modifier.size(16.dp).clip(CircleShape)
            )
            Text(
                text = "${eventData.location} | ",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Image(
                painter = painterResource(id = R.drawable.clock_icon),
                contentDescription = "Clock Icon",
                modifier = Modifier.size(20.dp).clip(CircleShape)
            )
            Text(
                text = eventData.readableTime(),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }


        Text(
            text = "Attending:",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy((-14).dp)
        )
        {
            for(attendee in eventData.attending.take(3))
            {
                Image(
                    painter = painterResource(id = R.drawable.defaultpfp),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(28.dp).clip(CircleShape)
                )
            }

        }

        Text(
            text = "Additional Details: ${eventData.description}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
        )

        /*
        Dialog box when user clicks join event
        Add to event then redirect to home screen on accept
         */
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        )
        {
            Button(
                modifier = Modifier.width(200.dp).height(67.dp),
                onClick = {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Are you sure you want to join ${eventData.title}?")
                        .setCancelable(false)
                        .setPositiveButton("Ok") { dialog, id ->
                            joinEvent(
                                navController,
                                eventData.id
                            )
                        }
                        .setNegativeButton("Cancel") { dialog, id ->
                            dialog.dismiss() // Close the dialog
                        }
                    val alert = builder.create()
                    alert.show()
                }
            )
            {
                Text(
                    text = "Join Event",
                    fontSize = 24.sp
                )
            }
        }
    }
}

fun joinEvent(navController: NavController, eventID: String)
{
    //TODO: add user to event
    //...


    navController.navigate("Home")
}