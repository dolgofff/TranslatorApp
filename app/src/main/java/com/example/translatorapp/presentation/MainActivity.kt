package com.example.translatorapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.presentation.navigation.auth.AuthState
import com.example.translatorapp.presentation.navigation.auth.AuthViewModel
import com.example.translatorapp.presentation.navigation.core.NavigationRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: AuthViewModel = hiltViewModel()
            val authState by viewModel.authState.collectAsStateWithLifecycle()

            splashScreen.setKeepOnScreenCondition {
                authState is AuthState.Loading
            }

            MaterialTheme {
                NavigationRoot(authState = authState)
            }
        }
    }
}