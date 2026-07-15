package com.example.translatorapp.data.media.camera

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer

class MLTextRecognizer(private val textRecognizer: TextRecognizer) {
    @ExperimentalGetImage
    fun process(image: ImageProxy, onTextRecognized: (Text) -> Unit, onComplete: () -> Unit) {
        val mediaImage = image.image ?: run {
            image.close()
            onComplete()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            image.imageInfo.rotationDegrees
        )

        textRecognizer
            .process(inputImage)
            .addOnSuccessListener { result ->
                onTextRecognized(result)
            }
            .addOnFailureListener {
                Log.e("OCR", "Recognition failed", it)
            }
            .addOnCompleteListener {
                image.close()
                onComplete()
            }
    }
}