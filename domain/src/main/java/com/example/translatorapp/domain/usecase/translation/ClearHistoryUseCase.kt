package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.repository.TranslationRepository

class ClearHistoryUseCase(private val translationRepository: TranslationRepository) {
    suspend operator fun invoke(): Result<Unit> = translationRepository.clearHistory()
}