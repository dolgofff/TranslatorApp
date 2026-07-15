package com.example.translatorapp.data.media.camera

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.example.translatorapp.data.mapper.entity.MlTextMapper
import com.example.translatorapp.domain.model.ml.RecognizedText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class FrameAnalyzer(
    private val mlTextRecognizer: MLTextRecognizer,
    private val mlTextMapper: MlTextMapper,
) {
    private val _recognizedText = MutableStateFlow<RecognizedText?>(null)
    val recognizedText = _recognizedText.asStateFlow()

    private val isProcessing = AtomicBoolean(false)

    @ExperimentalGetImage
    fun process(image: ImageProxy) {
        if (!isProcessing.compareAndSet(false, true)) {
            image.close()
            return
        }

        mlTextRecognizer.process(
            image = image,
            onTextRecognized = { mlText ->
                _recognizedText.value = mlTextMapper.map(mlText)
            },
            onComplete = {
                isProcessing.set(false)
            }
        )
    }
}