package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.model.Translation
import com.example.translatorapp.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ObserveTranslationsUseCase(private val translationRepository: TranslationRepository) {
    operator fun invoke(): Flow<Result<List<Translation>>> =
        translationRepository.observeCurrentTranslations()
            .map { Result.success(it) }
            .catch { error ->
                emit(Result.failure(error))
            }
}