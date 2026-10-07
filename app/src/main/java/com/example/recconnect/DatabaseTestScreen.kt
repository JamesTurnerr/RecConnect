package com.example.recconnect

import android.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun DatabaseTestScreen(navController: NavController) {
    val context = LocalContext.current
    val databaseTestViewModel: DatabaseTestViewModel = viewModel()

    LaunchedEffect(Unit) {
        databaseTestViewModel.loadEvents()
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        TextField(
            value = databaseTestViewModel.name,
            onValueChange = { databaseTestViewModel.updateName(it) },
            label = { Text("Enter name") },
            placeholder = { Text("Jeremy") }
        )

        TextField(
            value = databaseTestViewModel.email,
            onValueChange = { databaseTestViewModel.updateEmail(it) },
            label = { Text("Enter email") },
            placeholder = { Text("PromethiusUltraElitistPrimasMaximus@geemail.com") }
        )


        Button(//add user
            onClick = {
                val builder = AlertDialog.Builder(context)
                if(databaseTestViewModel.name == "")
                {
                    builder.setMessage("Name input is empty")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id -> dialog.dismiss()}
                }
                else if (databaseTestViewModel.email == "")
                {
                    builder.setMessage("Email input is empty")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id -> dialog.dismiss()}
                }
                else
                {
                    builder.setMessage("Adding name: ${databaseTestViewModel.name}, email: ${databaseTestViewModel.email}")
                        .setCancelable(false)
                        .setPositiveButton("Add") { dialog, id ->
                            databaseTestViewModel.createNewAccount()
                        }
                        .setNegativeButton("Cancel") { dialog, id ->
                            dialog.dismiss() // Close the dialog
                        }
                }
                val alert = builder.create()
                alert.show()
            }
        )
        {
            Text(text = "Add user to database")
        }



        TextField(
            value = databaseTestViewModel.eventName,
            onValueChange = { databaseTestViewModel.updateEventName(it) },
            label = { Text("Enter event name") },
            placeholder = { Text("Running") }
        )
        Button(//add event to database
            onClick = {
                val builder = AlertDialog.Builder(context)
                if(databaseTestViewModel.eventName == "")
                {
                    builder.setMessage("Name input is empty")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id -> dialog.dismiss()}
                }
                else
                {
                    builder.setMessage("Creating new event ${databaseTestViewModel.eventName}")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id -> databaseTestViewModel.createNewEvent()
                        }
                        .setNegativeButton("Cancel") { dialog, id ->
                            dialog.dismiss()
                        }

                }
                val alert = builder.create()
                alert.show()
            }
        )
        {
            Text(text = "Add new event")
        }


        Button(
            onClick = {
                databaseTestViewModel.loadEvents()
            }
        )
        {
            Text(text = "Console log events")
        }
        Row()
        {
            Button(
                onClick = {
                    databaseTestViewModel.addUserToEvent()
                }
            )
            {
                Text(text = "AAdd self to event")
            }
            TextField(
                value = databaseTestViewModel.eventIDinput,
                onValueChange = { databaseTestViewModel.updateEventIDinput(it) },
                label = { Text("Enter event ID") },
                placeholder = { Text("Enter event ID") }
            )

        }


        Button(
            onClick = {
                databaseTestViewModel.outputUserData()
            }
        )
        {
            Text(text = "Console log user data")
        }
        Button(
            onClick = {
                databaseTestViewModel.outputEventData()
            }
        )
        {
            Text(text = "Console log event data")
        }
        Button(
            onClick = {
                databaseTestViewModel.changeAccountName(Firebase.auth.currentUser?.uid ?: "1", "Billy bob bangles")
            }
        )
        {
            Text(text = "Change user '1's name")
        }
        Button(
            onClick = {
                databaseTestViewModel.getEventWithID("1")
            }
        )
        {
            Text(text = "Console log event with id '1'")
        }
        Button(
            onClick = {
                databaseTestViewModel.getJoinedEventIDs(Firebase.auth.currentUser?.uid ?: "1")
            }
        )
        {
            Text(text = "getJoinedEventIDs")
        }






    }
}