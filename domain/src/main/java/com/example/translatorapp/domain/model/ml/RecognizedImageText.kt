package com.example.translatorapp.domain.model.ml

data class RecognizedImageText(
    val width: Int,
    val height: Int,
    val recognizedText: RecognizedText,
)