package com.example.translatorapp.presentation.common

import com.example.translatorapp.domain.error.AuthError
import com.example.translatorapp.domain.error.TranslationError
import com.example.translatorapp.domain.error.VoiceRecognitionError
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

fun Throwable.toUiMessage(): String =
    when (this) {
        is AuthError.EmailAlreadyInUse -> "Email already in use"

        is AuthError.InvalidEmail -> "Invalid email address"

        is AuthError.EmptyCredentials -> "Empty credentials"

        is AuthError.WeakPassword -> "Password is too weak"

        is AuthError.WrongPassword -> "Wrong password"

        is AuthError.UserNotFound -> "User with such credentials was not found"

        is AuthError.NetworkError -> "Network error. Please try again"

        is AuthError.OAuthFailed -> "Invalid credentials"

        is AuthError.TooManyRequests -> "Too many attempts. Try again later"

        is AuthError.UserDisabled -> "User account is disabled"

        is AuthError.GoogleSignInCanceled -> "Authorisation was cancelled. Please, try again"

        is AuthError.Unknown -> errorMessage ?: "Cannot proceed an authorization right now"

        is TranslationError.NetworkError -> "Looks like the internet took a break!"

        is TranslationError.NotAuthenticated -> "This feature requires a login!"

        is TranslationError.PermissionDenied -> "We couldn't find this translation!"

        is TranslationError.InvalidData -> "Looks like there is a problem with data!"

        is TranslationError.ServerError -> "There was some problem on the server, try again!"

        is TranslationError.Unknown -> "Oops! Something went wrong!"

        is VoiceRecognitionError.ClientError -> "Something went wrong. Please try again."

        is VoiceRecognitionError.PermissionDenied -> "Microphone permission is required."

        is VoiceRecognitionError.NetworkError -> "Check your internet connection and try again."

        is VoiceRecognitionError.NoMatch -> "Couldn't recognize your speech. Please try again."

        is VoiceRecognitionError.Unknown -> "An unexpected error occurred."

        else -> "Oops, that wasn't supposed to happen..."
    }


/*fun AuthError.toUiMessage(): String =
    when (this) {
        is AuthError.EmailAlreadyInUse -> "Email already in use"

        is AuthError.InvalidEmail -> "Invalid email address"

        is AuthError.EmptyCredentials -> "Empty credentials"

        is AuthError.WeakPassword -> "Password is too weak"

        is AuthError.WrongPassword -> "Wrong password"

        is AuthError.UserNotFound -> "User with such credentials was not found"

        is AuthError.NetworkError -> "Network error. Please try again"

        is AuthError.OAuthFailed -> "Invalid credentials"

        is AuthError.TooManyRequests -> "Too many attempts. Try again later"

        is AuthError.UserDisabled -> "User account is disabled"

        is AuthError.GoogleSignInCanceled -> "Authorisation was cancelled. Please, try again"

        is AuthError.Unknown -> errorMessage ?: "Cannot proceed an authorization right now"*/

/*fun TranslationError.toUiMessage(): String =
    when (this) {
        is TranslationError.NetworkError ->
            "Looks like the internet took a break!"

        is TranslationError.NotAuthenticated ->
            "This feature requires a login!"

        is TranslationError.PermissionDenied ->
            "We couldn't find this translation!"

        is TranslationError.Unknown ->
            "Oops! Something went wrong!"

        is TranslationError.InvalidData ->
            "Looks like there is a problem with data!"

        is TranslationError.ServerError ->
            "There was some problem on the server, try again!"
    }*/

@OptIn(ExperimentalTime::class)
fun formatDateTime(timestamp: Instant): String {
    val zone = ZoneId.systemDefault()
    val itemDate = timestamp
        .toJavaInstant()
        .atZone(zone)
        .toLocalDate()

    val today = LocalDate.now(zone)
    val daysInterval = today.toEpochDay() - itemDate.toEpochDay()

    return when (daysInterval) {
        0L -> "Today"
        1L -> "Yesterday"
        else -> "$daysInterval days ago"
    }
}