package com.example.translatorapp.data.repository

import android.net.Uri
import com.example.translatorapp.data.media.camera.ImageTextRecognizer
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.ml.RecognizedImageText
import com.example.translatorapp.domain.repository.RecognitionRepository
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class RecognitionRepositoryImpl @Inject constructor(
    private val imageTextRecognizer: ImageTextRecognizer,
) : RecognitionRepository {
    override suspend fun recognizeImage(
        imageUri: String,
        sourceLanguage: LanguageCode,
    ): Result<RecognizedImageText> = imageTextRecognizer.recognize(
        uri = imageUri.toUri(),
        sourceLanguageCode = sourceLanguage.code
    )
}