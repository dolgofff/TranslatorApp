package com.example.translatorapp.presentation.navigation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translatorapp.domain.usecase.authorization.AuthStateUseCase
import com.example.translatorapp.presentation.navigation.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(authStateUseCase: AuthStateUseCase) : ViewModel() {
    val authState = authStateUseCase()
        .map { user ->
            if (user != null)
                AuthState.LoggedIn
            else
                AuthState.LoggedOut
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = AuthState.Loading
        )
}