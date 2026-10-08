package com.example.recconnect

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: com.example.recconnect.viewModels.AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // rememberSaveable keeps typed text across rotation / config changes
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }

    // Live validation results (only displayed once a field is touched or after submit)
    val firstNameError = Validators.name(firstName, "first name")
    val lastNameError = Validators.name(lastName, "last name")
    val emailError = Validators.email(email) ?: state.emailError   // state.emailError = from Firebase
    val passwordError = Validators.password(password)
    val confirmError = Validators.confirmPassword(password, confirm)

    fun submit() {
        submitted = true
        val allValid = listOf(
            firstNameError, lastNameError, emailError, passwordError, confirmError
        ).all { it == null }
        if (allValid) viewModel.register(firstName, lastName, email, password, confirm)
    }

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onRegistered()
    }

    AuthLayout {
        AuthHeader(
            title = stringResource(R.string.create_your_account),
            subtitle = stringResource(R.string.register_subtitle)
        )

        state.error?.let { ErrorBanner(it) }

        AuthTextField(
            value = firstName,
            onValueChange = { firstName = it; viewModel.clearError() },
            label = stringResource(R.string.first_name),
            error = firstNameError,
            showErrors = submitted,
            leadingIcon = Icons.Filled.Person
        )
        AuthTextField(
            value = lastName,
            onValueChange = { lastName = it; viewModel.clearError() },
            label = stringResource(R.string.last_name),
            error = lastNameError,
            showErrors = submitted,
            leadingIcon = Icons.Filled.Person
        )
        AuthTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            label = stringResource(R.string.email),
            error = emailError,
            showErrors = submitted,
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )
        AuthTextField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() },
            label = stringResource(R.string.password),
            error = passwordError,
            showErrors = submitted,
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            hint = stringResource(R.string.password_hint)
        )
        AuthTextField(
            value = confirm,
            onValueChange = { confirm = it; viewModel.clearError() },
            label = stringResource(R.string.confirm_password),
            error = confirmError,
            showErrors = submitted,
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            imeAction = ImeAction.Done,
            onDone = { submit() }
        )

        Spacer(Modifier.height(4.dp))
        PrimaryButton(text = stringResource(R.string.create_account), onClick = { submit() }, isLoading = state.isLoading)

        TextButton(onClick = onNavigateToLogin) {
            Text(stringResource(R.string.already_have_account))
        }
    }
}
