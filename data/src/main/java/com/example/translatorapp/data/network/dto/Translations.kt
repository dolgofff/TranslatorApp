package com.example.translatorapp.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Translations(
    @SerialName("possible-translations") val possibleTranslations: List<String>? = null,
)