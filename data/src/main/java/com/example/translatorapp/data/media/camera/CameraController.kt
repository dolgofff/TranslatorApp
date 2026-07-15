package com.example.translatorapp.data.media.camera

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
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

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalysis: ImageAnalysis? = null

    @OptIn(ExperimentalGetImage::class)
    suspend fun bind(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        cameraProvider = ProcessCameraProvider.getInstance(context).await()

        preview = Preview.Builder().build()
        preview?.surfaceProvider = previewView.surfaceProvider

        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()


        imageAnalysis?.setAnalyzer(cameraExecutor) { image ->
            frameAnalyzer.process(image)
        }

        cameraProvider?.unbindAll()
        camera = cameraProvider?.bindToLifecycle(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalysis
        )
    }

    fun setTorch(enabled: Boolean) {
        camera?.cameraControl?.enableTorch(enabled)
    }

    fun unbind() {
        cameraProvider?.unbindAll()
        camera = null
        preview = null
        imageAnalysis = null
    }
}