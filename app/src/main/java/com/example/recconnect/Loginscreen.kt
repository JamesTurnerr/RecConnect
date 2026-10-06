package com.example.recconnect

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }

    val emailError = Validators.email(email)
    val passwordError = Validators.loginPassword(password)

    fun submit() {
        submitted = true
        if (emailError == null && passwordError == null) viewModel.login(email, password)
    }

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoggedIn()
    }

    AuthLayout {
        AuthHeader(
            title = "Welcome back",
            subtitle = "Sign in to find your next workout"
        )

        // e.g. "Incorrect email or password" / no network
        state.error?.let { ErrorBanner(it) }

        AuthTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            label = "Email",
            error = emailError,
            showErrors = submitted,
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )
        AuthTextField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() },
            label = "Password",
            error = passwordError,
            showErrors = submitted,
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            imeAction = ImeAction.Done,
            onDone = { submit() }
        )

        Spacer(Modifier.height(4.dp))
        PrimaryButton(text = "Sign in", onClick = { submit() }, isLoading = state.isLoading)

        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? Create one")
        }
    }
}