package com.example.translatorapp.data.mapper

import com.example.translatorapp.domain.error.TranslationError
import com.example.translatorapp.domain.error.TranslationErrorMapper
import com.google.firebase.firestore.FirebaseFirestoreException

class TranslationErrorMapperImpl : TranslationErrorMapper {
    override operator fun invoke(exc: Throwable): TranslationError {
        return when (exc) {
            is FirebaseFirestoreException -> when (exc.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> TranslationError.PermissionDenied()
                FirebaseFirestoreException.Code.UNAUTHENTICATED -> TranslationError.NotAuthenticated()
                FirebaseFirestoreException.Code.UNAVAILABLE -> TranslationError.NetworkError()
                else -> TranslationError.Unknown(exc.message)
            }

            else -> TranslationError.Unknown(errorMessage = exc.message)
        }
    }
}