package com.example.translatorapp.domain.model.ml

data class RecognizedTextBlock(
    val text: String,
    val bounds: TextBounds?,
)
