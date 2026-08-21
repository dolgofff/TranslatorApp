package com.example.translatorapp.data.mapper.error

import com.example.translatorapp.domain.error.TranslationError
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiErrorMapper @Inject constructor() {
    operator fun invoke(exc: Exception): TranslationError {
        return when (exc) {
            is IOException -> TranslationError.NetworkError()

            is HttpException -> when (exc.code()) {
                401 -> TranslationError.NotAuthenticated()
                403 -> TranslationError.PermissionDenied()
                in 500..599 -> TranslationError.ServerError()
                else -> TranslationError.Unknown(exc.message())
            }

            is IllegalStateException -> TranslationError.InvalidData()

            else -> TranslationError.Unknown(exc.message)
        }
    }
}