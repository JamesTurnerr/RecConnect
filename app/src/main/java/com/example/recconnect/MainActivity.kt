package com.example.recconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import kotlinx.coroutines.flow.map

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

                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                val showBottomBar = currentRoute in listOf("Home", "Events", "Profile")

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) BottomNavBar(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "Home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("Home") {
                            HomeScreen(navController, eventViewModel)
                        }
                        composable("Events") {
                            EventsScreen(navController, eventViewModel)
                        }
                        composable("createEvent") {
                            CreateEventScreen(navController, eventViewModel)
                        }
                        composable(
                            "Event/{eventId}",
                            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                        ) { entry ->
                            val id = entry.arguments?.getString("eventId") ?: ""
                            // replace after merge!!
                            Text("Event detail coming soon")
                        }
                        composable("Profile") {
                            // replace after merge!!
                            Text("Profile coming soon")
                        }
                        composable("Settings") {
                            // after merging change to onLogout = { authViewModel.logout() }
                            SettingsScreen(navController, onLogout = { })
                        }
                    }
                }
            }
        }
    }
}