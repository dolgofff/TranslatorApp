package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.model.base.Translation
import com.example.translatorapp.domain.repository.TranslationRepository

class SaveTranslationUseCase(private val translationRepository: TranslationRepository) {
    suspend operator fun invoke(translation: Translation): Result<Unit> =
        translationRepository.saveTranslation(translation = translation)
}