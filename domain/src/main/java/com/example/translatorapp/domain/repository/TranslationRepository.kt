package com.example.translatorapp.domain.repository

import com.example.translatorapp.domain.model.Translation
import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    suspend fun saveTranslation(translation: Translation)

    suspend fun deleteTranslation(id: String)

    suspend fun clearHistory()
    suspend fun toggleFavourite(id: String, isFavourite: Boolean)

    fun observeCurrentTranslations(): Flow<List<Translation>>
}