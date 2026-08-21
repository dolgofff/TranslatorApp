package com.example.translatorapp.data.mapper.entity

import android.graphics.Rect
import com.example.translatorapp.domain.model.ml.RecognizedTextBlock
import com.example.translatorapp.domain.model.ml.TextBounds
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MlImageTextMapper @Inject constructor(private val languageIdentifier: LanguageIdentifier) {
    private companion object {
        const val MIN_OCR_CONFIDENCE = 0.60f
        const val MIN_LANGUAGE_CONFIDENCE = 0.45f
        const val MIN_TEXT_LENGTH = 2
        const val MIN_LETTERS_FOR_LANGUAGE_ID = 4
        const val MIN_WIDTH_PX = 20
        const val MIN_HEIGHT_PX = 10
        const val MIN_MEANINGFUL_RATIO = 0.60f
    }

    suspend fun map(text: Text, sourceLanguageCode: String): List<RecognizedTextBlock> =
        text.textBlocks
            .flatMap { block -> block.lines }
            .mapNotNull { line ->
                mapLine(line = line, sourceLanguageCode = sourceLanguageCode)
            }

    private suspend fun mapLine(
        line: Text.Line,
        sourceLanguageCode: String,
    ): RecognizedTextBlock? {
        val lineText = line.text.trim()

        if (lineText.isBlank())
            return null

        val bounds = line.boundingBox ?: return null

        if (!passesOcrConfidence(line))
            return null

        if (!passesBasicQualityFilter(lineText, bounds))
            return null

        if (!matchesSourceLanguage(text = lineText, sourceLanguageCode = sourceLanguageCode))
            return null

        return RecognizedTextBlock(
            text = line.text,
            bounds = TextBounds(
                left = bounds.left,
                top = bounds.top,
                right = bounds.right,
                bottom = bounds.bottom
            )
        )
    }

    private fun passesOcrConfidence(line: Text.Line): Boolean {
        val confidence = line.confidence

        if (confidence == 0f)
            return true

        return confidence >= MIN_OCR_CONFIDENCE
    }

    private fun passesBasicQualityFilter(text: String, bounds: Rect): Boolean {
        if (text.length < MIN_TEXT_LENGTH)
            return false

        if (bounds.width() < MIN_WIDTH_PX || bounds.height() < MIN_HEIGHT_PX)
            return false

        val meaningfulCharacters = text.count { char -> char.isLetterOrDigit() }

        if (meaningfulCharacters == 0)
            return false

        val meaningfulRatio = meaningfulCharacters.toFloat() / text.length.toFloat()

        return meaningfulRatio >= MIN_MEANINGFUL_RATIO
    }

    private suspend fun matchesSourceLanguage(text: String, sourceLanguageCode: String): Boolean {
        val letterCount = text.count { it.isLetter() }

        if (letterCount < MIN_LETTERS_FOR_LANGUAGE_ID)
            return true

        val expectedLanguage = sourceLanguageCode
            .substringBefore('-')
            .lowercase()

        val possibleLanguages = languageIdentifier
            .identifyPossibleLanguages(text)
            .await()

        val expectedCandidate = possibleLanguages
            .firstOrNull { candidate ->
                candidate.languageTag
                    .substringBefore('-')
                    .lowercase() == expectedLanguage
            } ?: return false

        return expectedCandidate.confidence >= MIN_LANGUAGE_CONFIDENCE
    }
}