package com.example.translatorapp.domain.model.ml

data class RecognizedText(
    val width: Int,
    val height: Int,
    val rotationDegrees: Int,
    val blocks: List<RecognizedTextBlock>,
)