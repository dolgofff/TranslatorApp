package com.example.translatorapp.data.mapper.error

import com.example.translatorapp.domain.error.TranslationError
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseTranslationErrorMapper @Inject constructor() {
    operator fun invoke(exc: Exception): TranslationError {
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