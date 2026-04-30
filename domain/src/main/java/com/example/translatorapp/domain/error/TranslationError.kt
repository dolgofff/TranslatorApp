package com.example.translatorapp.domain.error

sealed class TranslationError : Exception() {
    class NotAuthenticated : TranslationError()
    class PermissionDenied : TranslationError()
    class NetworkError : TranslationError()
    class ServerError : TranslationError()
    class InvalidData : TranslationError()

    data class Unknown(val errorMessage: String?) : TranslationError()
}