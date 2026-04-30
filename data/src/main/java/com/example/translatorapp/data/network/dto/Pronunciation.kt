package com.example.translatorapp.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pronunciation(
    @SerialName("destination-text-audio") val destinationTextAudio: String?,
    @SerialName("source-text-audio") val sourceTextAudio: String?,
)