package com.example.translatorapp.data.media.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.example.translatorapp.domain.model.ml.RecognizedText
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ExecutorService

class CameraController(
    private val context: Context,
    private val frameAnalyzer: FrameAnalyzer,
    private val cameraExecutor: ExecutorService,
) {
    val recognizedText: StateFlow<RecognizedText?> = frameAnalyzer.recognizedText

    private var controller: LifecycleCameraController? = null

    fun bind(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val controller = LifecycleCameraController(context)

        controller.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        controller.setImageAnalysisBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        controller.setImageAnalysisAnalyzer(cameraExecutor, frameAnalyzer.mlKitAnalyzer)

        Log.d("CameraController", "MlKitAnalyzer installed directly")

        previewView.controller = controller
        controller.bindToLifecycle(lifecycleOwner)

        this.controller = controller
    }

    fun setTorch(enabled: Boolean) {
        controller?.enableTorch(enabled)
    }

    fun unbind() {
        controller?.clearImageAnalysisAnalyzer()
        controller?.unbind()
        controller = null
    }
}
