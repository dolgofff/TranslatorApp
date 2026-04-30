package com.example.translatorapp.domain.repository

import com.example.translatorapp.domain.model.Translation
import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    suspend fun saveTranslation(translation: Translation): Result<Unit>

    suspend fun deleteTranslation(id: String): Result<Unit>

    suspend fun clearHistory(): Result<Unit>
    suspend fun toggleFavourite(id: String, isFavourite: Boolean): Result<Unit>

    fun observeCurrentTranslations(): Flow<List<Translation>>
}