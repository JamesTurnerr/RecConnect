package com.example.recconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recconnect.ui.theme.RecConnectTheme
import com.example.recconnect.viewModels.MainActivityViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.map
import com.example.recconnect.viewModels.AuthViewModel
import com.example.recconnect.viewModels.ProfileViewModel

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val darkMode by context.dataStore.data
                .map { it[SettingsKeys.DARK_MODE] ?: false }
                .collectAsState(initial = false)

            RecConnectTheme(darkTheme = darkMode) {
                val navController = rememberNavController()
                val route = navController.currentBackStackEntryAsState().value?.destination?.route
                val eventViewModel: EventViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                val profileViewModel: ProfileViewModel = viewModel()

                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                val showBottomBar = currentRoute in listOf("Home", "Events", "Profile")

                val startDestination = if (Firebase.auth.currentUser != null) "Home" else "Login"

                Scaffold(
                    topBar = {
                        if (route != "Register" && route != "Login") {
                            TopAppBar(
                                title = { Text("RecConnect") },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    },
                    bottomBar = {
                        if (showBottomBar) BottomNavBar(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("Login") {
                            LoginScreen(
                                onLoggedIn = {
                                    navController.navigate("Home") {
                                        popUpTo("Login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = { navController.navigate("Register") }
                            )
                        }
                        composable("Register") {
                            RegisterScreen(
                                onRegistered = {
                                    navController.navigate("Home") {
                                        popUpTo("Login") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = { navController.popBackStack() }
                            )
                        }
                        composable("Home") { HomeScreen(navController, eventViewModel) }
                        composable("Events") { EventsScreen(navController, eventViewModel) }
                        composable("createEvent") { CreateEventScreen(navController, eventViewModel) }
                        composable("Settings") {
                            SettingsScreen(
                                navController,
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("Login") {
                                        popUpTo(0)
                                    }
                                }
                            )
                        }
                        composable(
                            "Event/{eventId}",
                            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                        ) { entry ->
                            val id = entry.arguments?.getString("eventId") ?: ""
                            EventCard(navController, id)
                        }
                        composable("Profile") {
                            Profile(
                                navController,
                                profileViewModel,
                                Firebase.auth.currentUser?.uid ?: ""
                            )
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
                        composable(
                            "EditProfile/{userHandle}",
                            arguments = listOf(navArgument("userHandle") { type = NavType.StringType })
                        ) { entry ->
                            val handle = entry.arguments?.getString("userHandle") ?: ""
                            EditProfile(navController, profileViewModel, handle)
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

        Button(onClick = {
            Firebase.auth.signOut()
            navController.navigate("Register") {
                popUpTo("Home") { inclusive = true }   // back button can't return to Home
            }
        }) {
            Text("Log out")
        }

        Button(onClick = { navController.navigate("Profile/${Firebase.auth.currentUser?.uid}")}) {
            Text("go to profile")
        }

        Button(onClick = { navController.navigate("Profile/${"lDQe7p9nnzc5k9PD8O7BTxOT1Cw1"}")}) {
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