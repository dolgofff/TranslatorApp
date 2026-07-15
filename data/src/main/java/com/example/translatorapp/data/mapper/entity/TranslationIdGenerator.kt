package com.example.translatorapp.data.mapper.entity

import android.os.Build
import com.example.translatorapp.domain.model.base.Translation
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
        val zone = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZoneId.systemDefault()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val today = LocalDate.now(zone)

        return "${today.year}-${
            today.monthValue.toString().padStart(2, '0')
        }-${today.dayOfMonth.toString().padStart(2, '0')}"
    }
}