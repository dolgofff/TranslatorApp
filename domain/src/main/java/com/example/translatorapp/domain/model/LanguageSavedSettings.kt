package com.example.translatorapp.domain.model

data class LanguageSavedSettings(
    val savedSourceLanguage: LanguageCode,
    val savedDestinationLanguage: LanguageCode,
)