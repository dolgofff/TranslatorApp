package com.example.translatorapp.data.entity

data class TranslationEntity(
    val id: String,
    val sourceText: String? = null,
    val translatedText: String? = null,
    val sourceLanguage: String? = null,
    val targetLanguage: String? = null,
    val timestamp: Long? = null,
    val isFavourite: Boolean? = null,
)