package com.example.translatorapp.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranslationResponse(
    @SerialName("destination-text") val destinationText: String? = null,
    val pronunciation: Pronunciation? = null,
    val translations: Translations? = null,
)
