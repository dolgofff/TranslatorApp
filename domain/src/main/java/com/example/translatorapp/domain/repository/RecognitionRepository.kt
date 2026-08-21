package com.example.translatorapp.domain.repository

import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.ml.RecognizedImageText

interface RecognitionRepository {
    suspend fun recognizeImage(
        imageUri: String,
        sourceLanguage: LanguageCode,
    ): Result<RecognizedImageText>
}