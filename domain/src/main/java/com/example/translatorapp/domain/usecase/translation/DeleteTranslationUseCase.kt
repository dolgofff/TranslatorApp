package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.error.TranslationErrorMapper
import com.example.translatorapp.domain.repository.TranslationRepository

class DeleteTranslationUseCase(
    private val translationRepository: TranslationRepository,
    private val translationErrorMapper: TranslationErrorMapper,
) {
    suspend operator fun invoke(id: String) =
        try {
            translationRepository.deleteTranslation(id = id)
            Result.success(Unit)
        } catch (exc: Throwable) {
            Result.failure(translationErrorMapper(exc))
        }
}