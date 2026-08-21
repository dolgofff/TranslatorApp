package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.ml.RecognizedImageText
import com.example.translatorapp.domain.repository.RecognitionRepository

class RecognizeImageTextUseCase(private val recognitionRepository: RecognitionRepository) {
    suspend operator fun invoke(
        imageUri: String,
        sourceLanguage: LanguageCode,
    ): Result<RecognizedImageText> = recognitionRepository.recognizeImage(imageUri, sourceLanguage)
}