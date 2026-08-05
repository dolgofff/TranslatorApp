package com.example.translatorapp.data.media.camera

import android.graphics.Matrix
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.translatorapp.data.mapper.entity.MlTextMapper
import com.example.translatorapp.domain.model.ml.RecognizedText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class FrameAnalyzer(
    private val mlTextRecognizer: MLTextRecognizer,
    private val mlTextMapper: MlTextMapper,
) : ImageAnalysis.Analyzer {
    private val _recognizedText = MutableStateFlow<RecognizedText?>(null)
    val recognizedText = _recognizedText.asStateFlow()

    private var transformMatrix: Matrix? = null

    private val isProcessing = AtomicBoolean(false)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        if (!isProcessing.compareAndSet(false, true)) {
            image.close()
            return
        }

        mlTextRecognizer.process(
            image = image,
            onTextRecognized = { mlText ->
                val recognized = RecognizedText(
                    width = image.width,
                    height = image.height,
                    rotationDegrees = image.imageInfo.rotationDegrees,
                    blocks = mlTextMapper.map(text = mlText, transformMatrix = transformMatrix)
                )

                _recognizedText.value = recognized
            },
            onComplete = {
                isProcessing.set(false)
            }
        )
    }

    override fun updateTransform(matrix: Matrix?) {
        transformMatrix = matrix

        Log.d("FrameAnalyzer", "transformMatrix=$matrix")
    }

    override fun getTargetCoordinateSystem(): Int {
        return ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
    }
}