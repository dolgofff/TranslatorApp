package com.example.translatorapp.domain.media

import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.ml.DisplayedTextBlock
import com.example.translatorapp.domain.model.ml.RecognizedText
import com.example.translatorapp.domain.usecase.translation.TranslateTextUseCase

class CameraTextTranslator(private val translateTextUseCase: TranslateTextUseCase) {
    private val blockCache = mutableMapOf<CacheKey, String>()

    fun getDisplayedBlocks(
        recognizedText: RecognizedText,
        sourceLanguage: LanguageCode,
        destinationLanguage: LanguageCode,
    ): List<DisplayedTextBlock> {
        return recognizedText.blocks.map { block ->
            val normalizedText = block.text.trim()

            val key = CacheKey(
                sourceLanguage = sourceLanguage,
                destinationLanguage = destinationLanguage,
                text = normalizedText
            )

            val translatedText = blockCache[key] ?: block.text

            DisplayedTextBlock(
                originalText = block.text,
                translatedText = translatedText,
                bounds = block.bounds
            )
        }
    }

    suspend fun translate(
        recognizedText: RecognizedText,
        sourceLanguage: LanguageCode,
        destinationLanguage: LanguageCode,
    ) {
        recognizedText.blocks.forEach { block ->
            val text = block.text.trim()

            if (text.isBlank())
                return@forEach

            val key = CacheKey(
                sourceLanguage = sourceLanguage,
                destinationLanguage = destinationLanguage,
                text = text
            )

            if (blockCache.containsKey(key))
                return@forEach

            val translatedText = translateTextUseCase(
                sourceLanguage = sourceLanguage,
                destinationLanguage = destinationLanguage,
                text = text
            ).getOrNull()?.translatedText ?: block.text

            blockCache[key] = translatedText
        }
    }

    fun clearCache() {
        blockCache.clear()
    }

    private data class CacheKey(
        val sourceLanguage: LanguageCode,
        val destinationLanguage: LanguageCode,
        val text: String,
    )
}