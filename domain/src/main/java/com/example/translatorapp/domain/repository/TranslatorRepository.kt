package com.example.translatorapp.domain.repository

import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.base.Translation

interface TranslatorRepository {
    suspend fun translate(
        sourceLanguage: LanguageCode,
        destinationLanguage: LanguageCode,
        text: String,
    ): Result<Translation>
}