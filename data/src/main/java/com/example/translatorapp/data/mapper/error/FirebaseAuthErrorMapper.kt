package com.example.translatorapp.data.mapper.error

import com.example.translatorapp.domain.error.AuthError
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthErrorMapper @Inject constructor() {
    operator fun invoke(exc: Exception): AuthError {
        return when (exc) {
            is FirebaseAuthUserCollisionException -> AuthError.EmailAlreadyInUse()

            is FirebaseAuthWeakPasswordException -> AuthError.WeakPassword()

            is FirebaseAuthInvalidUserException -> when (exc.errorCode) {
                "ERROR_USER_NOT_FOUND" -> AuthError.UserNotFound()
                "ERROR_USER_DISABLED" -> AuthError.UserDisabled()

                else -> AuthError.Unknown(exc.message)
            }

            is FirebaseAuthInvalidCredentialsException -> when (exc.errorCode) {
                "ERROR_INVALID_EMAIL" -> AuthError.InvalidEmail()
                "ERROR_WRONG_PASSWORD" -> AuthError.WrongPassword()
                "ERROR_INVALID_CREDENTIAL" -> AuthError.OAuthFailed()

                else -> AuthError.Unknown(exc.message)
            }

            is FirebaseTooManyRequestsException -> AuthError.TooManyRequests()

            is FirebaseNetworkException -> AuthError.NetworkError()

            is ApiException -> when (exc.statusCode) {
                CommonStatusCodes.CANCELED -> AuthError.GoogleSignInCanceled()
                CommonStatusCodes.NETWORK_ERROR -> AuthError.NetworkError()

                else -> AuthError.OAuthFailed()
            }

            else -> AuthError.Unknown(exc.message)
        }
    }
}