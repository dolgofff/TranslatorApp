package com.example.translatorapp.data.mapper.entity

import com.example.translatorapp.domain.model.Translation
import java.time.LocalDate
import java.time.ZoneId

object TranslationIdGenerator {
    fun generateId(translation: Translation): String {
        val date = getTodayDateString()
        val sourceTextNormalized = translation.sourceText.trim().lowercase()
        val textHash = sourceTextNormalized.hashCode()

        return "${date}_${translation.sourceLanguage.code}_${translation.targetLanguage.code}_${textHash}"
    }

    private fun getTodayDateString(): String {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)

        return "${today.year}-${
            today.monthValue.toString().padStart(2, '0')
        }-${today.dayOfMonth.toString().padStart(2, '0')}"
    }
}