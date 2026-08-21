package com.example.translatorapp.data.media.camera

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import com.example.translatorapp.data.mapper.entity.MlTextMapper
import com.example.translatorapp.domain.model.ml.RecognizedText
import com.google.mlkit.vision.text.TextRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FrameAnalyzer @Inject constructor(
    private val textRecognizer: TextRecognizer,
    private val mlTextMapper: MlTextMapper,
    cameraExecutor: ExecutorService,
) {
    private val _recognizedText = MutableStateFlow<RecognizedText?>(null)
    val recognizedText: StateFlow<RecognizedText?> = _recognizedText.asStateFlow()

    val mlKitAnalyzer = MlKitAnalyzer(
        listOf(textRecognizer),
        ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
        cameraExecutor
    ) { result ->
        Log.d("MlKitAnalyzer", "callback")

        val text = result.getValue(textRecognizer) ?: return@MlKitAnalyzer

        Log.d(
            "OCR_RAW",
            text.textBlocks.joinToString("\n") { block ->
                "text='${block.text}', bounds=${block.boundingBox}"
            }
        )

        Log.d("MlKitAnalyzer", "text=${text.text}, blocks=${text.textBlocks.size}")

        val recognized = RecognizedText(blocks = mlTextMapper.map(text))

        Log.d("FrameAnalyzer", "recognized blocks=${recognized.blocks.size}")

        _recognizedText.value = recognized
    }
}