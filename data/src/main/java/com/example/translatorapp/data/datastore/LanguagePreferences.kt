package com.example.translatorapp.data.datastore

import kotlinx.serialization.Serializable

@Serializable
data class LanguagePreferences(
    val sourceLanguageCode: String = "en",
    val destinationLanguageCode: String = "ru"
)