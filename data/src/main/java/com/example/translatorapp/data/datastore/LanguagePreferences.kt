package com.example.translatorapp.data.datastore

import kotlinx.serialization.Serializable

@Serializable
data class LanguagePreferences(
    val sourceLanguageCode: String = "ru",
    val destinationLanguageCode: String = "en"
)