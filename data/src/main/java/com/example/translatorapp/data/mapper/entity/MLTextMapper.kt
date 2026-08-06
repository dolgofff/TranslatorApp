package com.example.translatorapp.data.mapper.entity

import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import com.example.translatorapp.domain.model.ml.RecognizedTextBlock
import com.example.translatorapp.domain.model.ml.TextBounds
import com.google.mlkit.vision.text.Text

class MlTextMapper() {
    fun map(text: Text, transformMatrix: Matrix?, cropRect: Rect): List<RecognizedTextBlock> {
        return text.textBlocks.map { block ->
            val bounds = block.boundingBox?.let { rect ->

                Log.d("MlTextMapper", "MLKit rect=$rect")

                val transformedRect = RectF(rect)
                Log.d(
                    "MlTextMapper",
                    "cropRect=$cropRect"
                )
                Log.d(
                    "MlTextMapper",
                    "rect before adjust=$rect"
                )
                transformMatrix?.mapRect(transformedRect)

                Log.d("MlTextMapper", "Transformed rect=$transformedRect")

                TextBounds(
                    left = transformedRect.left.toInt(),
                    top = transformedRect.top.toInt(),
                    right = transformedRect.right.toInt(),
                    bottom = transformedRect.bottom.toInt()
                )
            }

            RecognizedTextBlock(
                text = block.text,
                bounds = bounds
            )
        }
    }
}