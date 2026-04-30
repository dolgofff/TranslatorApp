package com.example.translatorapp.presentation.navigation.auth

sealed interface AuthState {
    object Loading : AuthState
    object LoggedOut : AuthState
    object LoggedIn : AuthState
}