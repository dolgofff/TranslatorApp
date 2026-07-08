package com.example.translatorapp.domain.model

import com.example.translatorapp.domain.model.language.LanguageCode
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Translation(
    val id: String,
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: LanguageCode,
    val targetLanguage: LanguageCode,
    val timestamp: Instant,
    val isFavourite: Boolean,
    val sourceAudioUrl: String? = null,
    val destinationAudioUrl: String? = null,
)