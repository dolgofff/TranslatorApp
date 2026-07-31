package com.example.translatorapp.domain.model.ml

data class DisplayedTextBlock(
    val originalText: String,
    val translatedText: String,
    val bounds: TextBounds?
)