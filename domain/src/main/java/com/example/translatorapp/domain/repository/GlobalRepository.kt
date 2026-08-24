package com.example.translatorapp.domain.repository

import com.example.translatorapp.domain.model.language.LanguageSavedSettings
import kotlinx.coroutines.flow.Flow

interface GlobalRepository {
    val preferencesFlow: Flow<LanguageSavedSettings>

    suspend fun setSourceLanguage(code: String)

    suspend fun setDestinationLanguage(code: String)

    suspend fun setLanguages(sourceCode: String, destinationCode: String)
}