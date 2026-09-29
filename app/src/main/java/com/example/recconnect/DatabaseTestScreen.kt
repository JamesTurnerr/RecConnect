package com.example.recconnect

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun DatabaseTestScreen(navController: NavController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        var nameInput by remember { mutableStateOf("") }
        TextField(
            value = nameInput,
            onValueChange = { nameInput = it }, // Updates state as user types
            label = { Text("Enter name") },
            placeholder = { Text("John Doe") }
        )

        var emailInput by remember { mutableStateOf("") }
        TextField(
            value = emailInput,
            onValueChange = { emailInput = it }, // Updates state as user types
            label = { Text("Enter email") },
            placeholder = { Text("JohnDoe@geemail.com") }
        )


        Button(//add user
            onClick = {
                val builder = AlertDialog.Builder(context)
                if(nameInput == "")
                {
                    builder.setMessage("Name input is empty")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id -> dialog.dismiss()}
                }
                else if (emailInput == "")
                {
                    builder.setMessage("Email input is empty")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id -> dialog.dismiss()}
                }
                else
                {
                    builder.setMessage("Adding name: $nameInput, email: $emailInput")
                        .setCancelable(false)
                        .setPositiveButton("Add") { dialog, id ->
                            dbAddUser(nameInput, emailInput)
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

        Button(//check email exists
            onClick = {
                val builder = AlertDialog.Builder(context)
                if(nameInput == "")
                {
                    builder.setMessage("Name input is empty")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id -> dialog.dismiss()}
                }
                else if (emailInput == "")
                {
                    builder.setMessage("Email input is empty")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id -> dialog.dismiss()}
                }
                else
                {
                    if(dbEmailExists(emailInput))
                    {
                        builder.setMessage("$emailInput exists")
                            .setCancelable(false)
                            .setPositiveButton("OK") { dialog, id -> dialog.dismiss()}
                    }
                    else
                    {
                        builder.setMessage("$emailInput does not exist")
                            .setCancelable(false)
                            .setPositiveButton("OK") { dialog, id -> dialog.dismiss()}
                    }

                }
                val alert = builder.create()
                alert.show()
            }
        )
        {
            Text(text = "Check if email exists in database")
        }
    }
}

fun dbAddUser(name: String, email: String)
{
    val db = Firebase.firestore
    val user = hashMapOf(
        "name" to name,
        "email" to email
    )

    db.collection("users")
        .add(user)
        .addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
}

fun dbEmailExists(email: String): Boolean
{
    return false//need to implement logic
}