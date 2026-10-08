package com.example.recconnect

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun EditProfile(
    navController: NavController,
    viewModel: com.example.recconnect.viewModels.ProfileViewModel,
    userHandle: String
) {
    val profile by viewModel.profile.collectAsState()

    var name by rememberSaveable {
        mutableStateOf(profile?.name ?: "")
    }

    var email by rememberSaveable {
        mutableStateOf(profile?.email ?: "")
    }

    var showSaveDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(userHandle) {
        viewModel.loadProfile(userHandle)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(18.dp))
            }

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
                            contentDescription = stringResource(R.string.back)
                        )
                    }

                    Text(
                        text = stringResource(R.string.Edit_profile),
                        fontSize = 30.sp,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text(stringResource(R.string.name))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = {
                        Text(stringResource(R.string.email))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true
                )
            }

            item {
                val snackbarMessage = stringResource(R.string.name_email_empty)
                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank()) {

                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    snackbarMessage
                                )
                            }
                        } else {
                            showSaveDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.save_changes))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFFF44336),
                contentColor = Color.White
            )
        }
    }

    val failSnackbarMessage = stringResource(R.string.profile_update_fail)

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = {
                showSaveDialog = false
            },
            title = {
                Text(stringResource((R.string.save_changes)))
            },
            text = {
                Text(stringResource(R.string.save_changes_message))
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateProfile(
                            email = email,
                            name = name,
                            onSuccess = {
                                showSaveDialog = false

                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("profileUpdated", true)

                                navController.popBackStack()
                            },
                            onError = {
                                showSaveDialog = false

                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        failSnackbarMessage
                                    )
                                }
                            }
                        )
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSaveDialog = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}