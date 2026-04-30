package com.example.translatorapp.data.mapper.entity

import com.example.translatorapp.data.entity.TranslationEntity
import com.example.translatorapp.data.network.dto.TranslationResponse
import com.example.translatorapp.domain.model.LanguageCode
import com.example.translatorapp.domain.model.Translation
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun TranslationResponse.toDomainTranslation(
    sourceText: String,
    sourceLanguage: LanguageCode,
    targetLanguage: LanguageCode,
): Translation {
    val translatedText = translations?.possibleTranslations
        ?.firstOrNull()
        .orEmpty()

    val sourceAudioUrl = pronunciation?.sourceTextAudio
    val destinationAudioUrl = pronunciation?.destinationTextAudio

    return Translation(
        id = "",
        sourceText = sourceText,
        translatedText = translatedText,
        sourceLanguage = sourceLanguage,
        targetLanguage = targetLanguage,
        timestamp = Clock.System.now(),
        isFavourite = false,
        sourceAudioUrl = sourceAudioUrl,
        destinationAudioUrl = destinationAudioUrl
    )
}

@OptIn(ExperimentalTime::class)
fun Translation.toEntityTranslation(): TranslationEntity =
    TranslationEntity(
        id = TranslationIdGenerator.generateId(this),
        sourceText = sourceText,
        translatedText = translatedText,
        sourceLanguage = sourceLanguage.code,
        targetLanguage = targetLanguage.code,
        timestamp = timestamp.toEpochMilliseconds(),
        isFavourite = isFavourite
    )

@OptIn(ExperimentalTime::class)
fun TranslationEntity.toDomainTranslation(id: String): Translation =
    Translation(
        id = id,
        sourceText = sourceText.orEmpty(),
        translatedText = translatedText.orEmpty(),
        sourceLanguage = LanguageCode.fromCode(sourceLanguage) ?: LanguageCode.ENGLISH,
        targetLanguage = LanguageCode.fromCode(targetLanguage) ?: LanguageCode.RUSSIAN,
        timestamp = Instant.fromEpochMilliseconds(
            timestamp ?: Clock.System.now().toEpochMilliseconds()
        ),
        isFavourite = isFavourite ?: false
    )