package com.example.translatorapp.domain.media

import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.ml.DisplayedTextBlock
import com.example.translatorapp.domain.model.ml.RecognizedText
import com.example.translatorapp.domain.usecase.translation.TranslateTextUseCase
import kotlin.time.TimeSource

class CameraTextTranslator(private val translateTextUseCase: TranslateTextUseCase) {
    private val blockCache = mutableMapOf<CacheKey, String>()

    private var lastRecognizedText = ""

    private var lastDisplayedBlocks = emptyList<DisplayedTextBlock>()

    private val timeSource = TimeSource.Monotonic

    private var lastTranslationMark = timeSource.markNow()

    private companion object {
        const val MIN_TRANSLATION_INTERVAL_MS = 700L
    }

    suspend fun translate(
        recognizedText: RecognizedText,
        sourceLanguage: LanguageCode,
        destinationLanguage: LanguageCode,
    ): List<DisplayedTextBlock> {

        val currentText = recognizedText.blocks
            .joinToString("\n") { it.text.trim() }
            .trim()

        if (currentText.isBlank()) {
            return emptyList()
        }

        if (currentText == lastRecognizedText) {
            return lastDisplayedBlocks
        }

        if (
            lastTranslationMark.elapsedNow().inWholeMilliseconds <
            MIN_TRANSLATION_INTERVAL_MS
        ) {
            return lastDisplayedBlocks
        }

        val translatedBlocks = recognizedText.blocks.map { block ->

            val translatedText = translateBlock(
                text = block.text,
                sourceLanguage = sourceLanguage,
                destinationLanguage = destinationLanguage
            )

            DisplayedTextBlock(
                originalText = block.text,
                translatedText = translatedText,
                bounds = block.bounds
            )
        }

        lastRecognizedText = currentText
        lastDisplayedBlocks = translatedBlocks
        lastTranslationMark = timeSource.markNow()

        return translatedBlocks
    }

    fun clearCache() {
        blockCache.clear()
        lastRecognizedText = ""
        lastDisplayedBlocks = emptyList()
        lastTranslationMark = timeSource.markNow()
    }

    private suspend fun translateBlock(
        text: String,
        sourceLanguage: LanguageCode,
        destinationLanguage: LanguageCode,
    ): String {

        val key = CacheKey(
            sourceLanguage = sourceLanguage,
            destinationLanguage = destinationLanguage,
            text = text.trim()
        )

        blockCache[key]?.let { return it }

        val translated = translateTextUseCase(
            sourceLanguage = sourceLanguage,
            destinationLanguage = destinationLanguage,
            text = text
        ).getOrNull()?.translatedText ?: text

        blockCache[key] = translated

        return translated
    }


    private data class CacheKey(
        val sourceLanguage: LanguageCode,
        val destinationLanguage: LanguageCode,
        val text: String,
    )
}