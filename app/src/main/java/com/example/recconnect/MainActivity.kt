package com.example.recconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.recconnect.ui.theme.RecConnectTheme
import androidx.navigation.compose.composable


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RecConnectTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination =  "Profile"){
                    composable("Profile") {
                        Profile(
                            navController,
                            "Mel123",
                            "MEL"
                        )
                    }

                    composable("ProfileEdit") {
                        EditProfile(
                            navController,
                            "Mel123",
                            "MEL"

                        )
                    }
                }


            }
        }
    }
}

