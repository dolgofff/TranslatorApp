package com.example.translatorapp.presentation.screen.registration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.presentation.ui.components.RegistrationForm
import com.example.translatorapp.presentation.ui.components.RegistrationHeaderImage
import com.example.translatorapp.presentation.ui.components.RegistrationTitle

@Composable
fun RegistrationScreen(
    viewmodel: RegistrationViewModel = hiltViewModel(),
    onSignUpClick: () -> Unit,
    onSignInNavClick: () -> Unit,
) {
    val state by viewmodel.registrationState.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            RegistrationHeaderImage()

            RegistrationTitle()

            Spacer(Modifier.height(24.dp))

            RegistrationForm(
                email = state.email,
                onEmailChange = viewmodel::updateEmailText,
                password = state.email,
                onPasswordChange = viewmodel::updatePasswordText,
                passwordVisible = state.visibility1,
                onPasswordVisibilityChange = viewmodel::togglePasswordVisibility1,
                confirmPassword = state.confirmPassword,
                onConfirmPasswordChange = viewmodel::updateConfirmPasswordText,
                confirmPasswordVisible = state.visibility2,
                onConfirmPasswordVisibilityChange = viewmodel::togglePasswordVisibility2,
                onRegisterClick = { viewmodel.register(onSuccess = onSignUpClick) },
                onLoginClick = onSignInNavClick,
                errorText = state.errorMessage
            )
        }
    }
}