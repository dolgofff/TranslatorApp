package com.example.translatorapp.presentation.screen.login

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.presentation.ui.components.LoginForm
import com.example.translatorapp.presentation.ui.components.LoginHeaderImage
import com.example.translatorapp.presentation.ui.components.LoginTitle

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onSignInClick: () -> Unit,
    onSignUpNavClick: () -> Unit,
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val handler = remember { GoogleSignInHandler(context = context) }
    var googleSignInRequested by remember { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(googleSignInRequested) {
        if (googleSignInRequested == 0) return@LaunchedEffect

        Log.d("GoogleSignIn", "LaunchedEffect triggered")

        val idToken = handler.requestIdToken()

        Log.d("GoogleSignIn", "Token received: $idToken")

        if (idToken != null) {
            viewModel.signInWithGoogle(idToken = idToken, onSuccess = onSignInClick)
        } else {
            viewModel.onGoogleSignInFailed()
        }
    }

    LaunchedEffect(state.resetPasswordEmailSent) {
        if (state.resetPasswordEmailSent) {
            snackbarHostState.showSnackbar(message = "Password reset message was sent")
            viewModel.updateResetPasswordEvent()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LoginHeaderImage()

            LoginTitle()

            Spacer(Modifier.height(24.dp))

            LoginForm(
                email = state.email,
                onEmailChange = viewModel::updateEmailText,
                password = state.password,
                onPasswordChange = viewModel::updatePasswordText,
                passwordVisible = state.visibility,
                onPasswordVisibilityChange = viewModel::togglePasswordVisibility,
                onLoginClick = { viewModel.signInEmail(onSuccess = onSignInClick) },
                onGoogleLoginClick = { googleSignInRequested++ },
                errorText = state.errorMessage,
                onSignUpClick = onSignUpNavClick,
                onPasswordResetClick = viewModel::resetPassword
            )
        }
    }
}