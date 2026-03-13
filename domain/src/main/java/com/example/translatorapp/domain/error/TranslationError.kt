package com.example.translatorapp.domain.error

sealed class TranslationError : Throwable() {
    class NotAuthenticated : TranslationError()
    class PermissionDenied : TranslationError()
    class NetworkError : TranslationError()

    data class Unknown(val errorMessage: String?) : TranslationError()
}