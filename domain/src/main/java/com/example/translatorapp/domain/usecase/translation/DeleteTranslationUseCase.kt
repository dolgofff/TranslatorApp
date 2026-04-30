package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.repository.TranslationRepository

class DeleteTranslationUseCase(private val translationRepository: TranslationRepository) {
    suspend operator fun invoke(id: String): Result<Unit> =
        translationRepository.deleteTranslation(id = id)
}