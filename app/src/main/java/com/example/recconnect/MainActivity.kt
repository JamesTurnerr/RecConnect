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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recconnect.ui.theme.RecConnectTheme
import com.example.recconnect.viewModels.MainActivityViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private val profileViewModel: com.example.recconnect.viewModels.ProfileViewModel by viewModels()

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
                        val startDestination = if (Firebase.auth.currentUser != null) "Home" else "Register"

                        NavHost(navController = navController, startDestination = startDestination) {
                            composable("Register") {
                                RegisterScreen(
                                    onRegistered = {
                                        navController.navigate("Home") { popUpTo("Register") { inclusive = true } }
                                    },
                                    onNavigateToLogin = { navController.navigate("Login") }   // replaces the commented-out line
                                )
                            }

                            composable("Login") {
                                LoginScreen(
                                    onLoggedIn = {
                                        navController.navigate("Home") { popUpTo("Register") { inclusive = true } }
                                    },
                                    onNavigateToRegister = { navController.popBackStack() }
                                )
                            }
                            composable("Home") {
                                Home(navController = navController)
                            }

                            composable(
                                "EventCard/{eventId}",
                                arguments = listOf(navArgument("eventId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val eventId = backStackEntry.arguments?.getString("eventId")
                                eventId?.let { EventCard(navController, it) }
                            }

                            composable("DatabaseTest") {
                                DatabaseTestScreen(navController = navController)
                            }

                            composable("Profile/{userId}") { backStackEntry ->

                                val userId = backStackEntry.arguments?.getString("userId")
                                if (userId != null) {
                                    Profile(
                                        navController = navController,
                                        viewModel = profileViewModel,
                                        userHandle = userId
                                    )
                                }
                            }

                            composable("EditProfile/{userHandle}") { backStackEntry ->

                                val userHandle =
                                    backStackEntry.arguments?.getString("userHandle") ?: ""

                                EditProfile(
                                    navController = navController,
                                    viewModel = profileViewModel,
                                    userHandle = userHandle
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
    val mainActivityViewModel: MainActivityViewModel = viewModel()
    val events by mainActivityViewModel.events.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Homepage/Navigation Testing\nUser ID: ${Firebase.auth.currentUser?.uid}")

        Button(onClick = { navController.navigate("EventCard/1") }) {
            Text("Go to Event 1")
        }
        Button(onClick = { navController.navigate("EventCard/2") }) {
            Text("Go to Event 2")
        }

        Button(onClick = { navController.navigate("DatabaseTest") }) {
            Text("Go to database test screen")
        }


        Button(onClick = { navController.navigate("DatabaseTest") }) {
            Text("Go to database test screen")
        }

        Button(onClick = { navController.navigate("Profile/${Firebase.auth.currentUser?.uid}")}) {
            Text("go to profile")
        }

        Button(onClick = { navController.navigate("Profile/${"zDsiLACuUloJMMy4wMt2"}")}) {
            Text("test profile not logged into")
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            items(events) { event ->
                Button(onClick = { navController.navigate("EventCard/${event.id}") })
                {
                    Text(text = event.title + ":" + event.id)
                }

            }
        }

    }
}