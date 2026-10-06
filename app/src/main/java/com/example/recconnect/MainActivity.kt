package com.example.recconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recconnect.ui.theme.RecConnectTheme

class MainActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecConnectTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("RecConnect") },
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(navController = navController, startDestination = "Home") {
                            composable("Home") {
                                Home(navController = navController)
                            }

                            composable(
                                "Event/{eventId}",
                                arguments = listOf(navArgument("eventId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val eventId = backStackEntry.arguments?.getString("eventId")
                                eventId?.let { Event(navController, it) }
                            }

                            composable("DatabaseTest") {
                                DatabaseTestScreen(navController = navController)
                            }

                            composable("Profile") {
                                Profile(
                                    navController = navController,
                                    viewModel = profileViewModel,
                                    userHandle = "placeholder"  //change to real handle
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Home(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Homepage/Navigation Testing")

        Button(onClick = { navController.navigate("Event/1") }) {
            Text("Go to Event 1")
        }
        Button(onClick = { navController.navigate("Event/2") }) {
            Text("Go to Event 2")
        }

        Button(onClick = { navController.navigate("DatabaseTest") }) {
            Text("Go to database test screen")
        }

        Button(onClick = { navController.navigate("Profile")}) {
            Text("go to profile")
        }

    }
}