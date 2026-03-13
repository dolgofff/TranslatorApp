package com.example.translatorapp.data.mapper

import com.example.translatorapp.data.entity.TranslationEntity
import com.example.translatorapp.domain.model.Translation
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun TranslationEntity.toDomainTranslation(id: String): Translation =
    Translation(
        id = id,
        sourceText = sourceText.orEmpty(),
        translatedText = translatedText.orEmpty(),
        sourceLanguage = sourceLanguage.orEmpty(),
        targetLanguage = targetLanguage.orEmpty(),
        timestamp = Instant.fromEpochMilliseconds(timestamp ?: 0),
        isFavourite = isFavourite ?: false
    )

@OptIn(ExperimentalTime::class)
fun Translation.toEntityTranslation(): TranslationEntity =
    TranslationEntity(
        sourceText = sourceText,
        translatedText = translatedText,
        sourceLanguage = sourceLanguage,
        targetLanguage = targetLanguage,
        timestamp = timestamp.toEpochMilliseconds(),
        isFavourite = isFavourite
    )