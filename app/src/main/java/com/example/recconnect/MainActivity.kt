package com.example.recconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recconnect.ui.theme.RecConnectTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.map
import com.example.recconnect.viewModels.AuthViewModel
import com.example.recconnect.viewModels.ProfileViewModel

class MainActivity : ComponentActivity() {
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
                val eventViewModel: EventViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                val profileViewModel: ProfileViewModel = viewModel()

                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                val showBottomBar = currentRoute in listOf("Home", "Events", "Profile")

                val startDestination = if (Firebase.auth.currentUser != null) "Home" else "Login"

                Scaffold(
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