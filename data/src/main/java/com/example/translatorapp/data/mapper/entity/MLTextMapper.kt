package com.example.translatorapp.data.mapper.entity

import com.example.translatorapp.domain.model.ml.RecognizedText
import com.example.translatorapp.domain.model.ml.RecognizedTextBlock
import com.example.translatorapp.domain.model.ml.TextBounds
import com.google.mlkit.vision.text.Text

class MlTextMapper() {
    fun map(text: Text): RecognizedText =
        RecognizedText(blocks = text.textBlocks.map { block ->
            RecognizedTextBlock(
                text = block.text,
                bounds = block.boundingBox?.let { rect ->
                    TextBounds(
                        left = rect.left,
                        top = rect.top,
                        right = rect.right,
                        bottom = rect.bottom
                    )
                }
            )
        }
        )
}