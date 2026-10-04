package com.example.recconnect

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(
    navController: NavController,
    currentUserHandle: String,
    currentName: String


    ){
    var name by rememberSaveable { mutableStateOf(currentName) }
    var userHandle by rememberSaveable { mutableStateOf(currentUserHandle) }
    var showSaveDialog by rememberSaveable { mutableStateOf(false)}

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)

    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "back"
                    )
                }


                Text(
                    text = "Edit Profile",
                    fontSize = 30.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            OutlinedTextField(
                value = userHandle,
                onValueChange = { userHandle = it},
                label = {
                    Text("User Handle")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it},
                label = {
                    Text("Name")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )
        }

        item  {
            Button(
                onClick = {
                    showSaveDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Save Changes")
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = {
                showSaveDialog = false
            },
            title = {
                Text("Save Changes?")
            },
            text = {
                Text("Are you sure you want to save these changes?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // update the database here

                        showSaveDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSaveDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}