package com.example.translatorapp.domain.model.language

data class LanguageSavedSettings(
    val savedSourceLanguage: LanguageCode,
    val savedDestinationLanguage: LanguageCode,
)