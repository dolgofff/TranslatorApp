package com.example.translatorapp.data.repository

import android.util.Log
import com.example.translatorapp.data.mapper.entity.toDomainTranslation
import com.example.translatorapp.data.mapper.error.ApiErrorMapper
import com.example.translatorapp.data.network.api.TranslationApi
import com.example.translatorapp.domain.error.TranslationError
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.Translation
import com.example.translatorapp.domain.repository.TranslatorRepository
import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class TranslatorRepositoryImpl(
    private val api: TranslationApi,
    private val errorMapper: ApiErrorMapper,
) : TranslatorRepository {
    override suspend fun translate(
        sourceLanguage: LanguageCode,
        destinationLanguage: LanguageCode,
        text: String,
    ): Result<Translation> =
        try {
            val response = retryRequest {
                api.translate(
                    sourceLanguage = sourceLanguage.code,
                    destinationLanguage = destinationLanguage.code,
                    text = text
                )
            }

            val translation = response.toDomainTranslation(
                sourceText = text,
                sourceLanguage = sourceLanguage,
                targetLanguage = destinationLanguage
            )

            if (translation.translatedText.isBlank()) {
                Result.failure(TranslationError.InvalidData())
            } else {
                Result.success(translation)
            }
        } catch (exc: Exception) {
            if (exc is CancellationException) throw exc

            Result.failure(errorMapper(exc))
        }
}

private suspend fun <T> retryRequest(
    times: Int = 3,
    initialDelay: Long = 500,
    factor: Double = 2.0,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelay

    repeat(times - 1) {
        try {
            return block()
        } catch (exc: Exception) {
            if (exc is CancellationException) throw exc

            if (exc is IOException) {
                Log.e("TranslatorRepository", "Retry due to network error", exc)
            }
        }

        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong()
    }
    return block()
}
