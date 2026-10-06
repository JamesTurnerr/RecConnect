package com.example.recconnect

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Profile(
    navController: NavController,
    viewModel: ProfileViewModel,
    userHandle: String
) {
    val profile by viewModel.profile.collectAsState()

    LaunchedEffect(userHandle) {
        viewModel.loadProfile(userHandle)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)

    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                    text = "Profile",
                    fontSize = 30.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.defaultpfp),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(125.dp).clip(CircleShape)
                )

                Column(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = profile?.name ?: "Loading...",
                        fontSize = 35.sp
                    )
                    Text(
                        text = "@${profile?.userHandle ?: userHandle}",
                        fontSize = 18.sp
                    )
                }

                //todo only allow the edit option if this is the user logged in
                IconButton(
                    onClick = {
                        navController.navigate("ProfileEdit")
                    }
                ) {
                     Icon(
                        imageVector = Icons.Default.ModeEdit,
                        contentDescription = "edit profile"
                    )
                }
            }

        }

        item {
            HorizontalDivider(thickness = 3.dp,
            )
        }

        item {
            Text(
                text = "activities attending"
                // todo fetch these from the database
                // for each compose element
                // create a compose for these or reuses the on for the main page
            )
        }

    }
}