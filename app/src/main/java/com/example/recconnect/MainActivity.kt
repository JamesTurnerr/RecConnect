package com.example.recconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recconnect.ui.theme.RecConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecConnectTheme {
                val navController = rememberNavController()
                val eventViewModel: EventViewModel = viewModel()
                val currentUserId = "test-user-id"
                val currentUserName = "Test User"
                Scaffold(
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = "Events"
                        ) {
                            composable("Events") {
                                EventsScreen(
                                    navController = navController,
                                    eventViewModel = eventViewModel,
                                )
                            }
                            composable("createEvent") {
                                CreateEventScreen(
                                    navController = navController,
                                    eventViewModel = eventViewModel,
                                    currentUserId = currentUserId,
                                    currentUserName = currentUserName
                                )
                            }
                            composable("eventDetail/{eventId}") {
                                Text("Event detail coming soon!!!!")
                            }
                        }
                    }
                }
            }
        }
    }
}