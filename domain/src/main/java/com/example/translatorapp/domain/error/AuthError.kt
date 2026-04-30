package com.example.translatorapp.domain.error

sealed class AuthError : Exception() {
    class EmailAlreadyInUse : AuthError()
    class EmptyCredentials : AuthError()
    class WeakPassword : AuthError()
    class InvalidEmail : AuthError()
    class WrongPassword : AuthError()
    class UserNotFound : AuthError()
    class UserDisabled : AuthError()
    class NetworkError : AuthError()
    class TooManyRequests : AuthError()
    class GoogleSignInCanceled : AuthError()
    class OAuthFailed : AuthError()

    data class Unknown(val errorMessage: String?) : AuthError()
}