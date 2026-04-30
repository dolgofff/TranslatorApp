package com.example.translatorapp.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranslationResponse(
    val pronunciation: Pronunciation?,
    val translations: Translations?,
)
