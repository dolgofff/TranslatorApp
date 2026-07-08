package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.Translation
import com.example.translatorapp.domain.repository.TranslatorRepository

class TranslateTextUseCase(private val translatorRepository: TranslatorRepository) {
    suspend operator fun invoke(
        sourceLanguage: LanguageCode,
        destinationLanguage: LanguageCode,
        text: String,
    ): Result<Translation> =
        translatorRepository.translate(
            sourceLanguage = sourceLanguage,
            destinationLanguage = destinationLanguage,
            text = text
        )
}