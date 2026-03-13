package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.error.TranslationErrorMapper
import com.example.translatorapp.domain.model.Translation
import com.example.translatorapp.domain.repository.TranslationRepository

class SaveTranslationUseCase(
    private val translationRepository: TranslationRepository,
    private val translationErrorMapper: TranslationErrorMapper,
) {
    suspend operator fun invoke(translation: Translation) =
        try {
            translationRepository.saveTranslation(translation = translation)
            Result.success(Unit)
        } catch (exc: Throwable) {
            Result.failure(translationErrorMapper(exc))
        }
}