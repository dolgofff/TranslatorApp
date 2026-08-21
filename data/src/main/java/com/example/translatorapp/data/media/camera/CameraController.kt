package com.example.translatorapp.data.media.camera

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.translatorapp.domain.model.ml.RecognizedText
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraController @Inject constructor(
    @param:ApplicationContext private val context: Context,
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

    fun takePicture(onSuccess: (Uri) -> Unit, onError: (Throwable) -> Unit) {
        val controller = controller

        if (controller == null) {
            onError(IllegalStateException("Camera is not bound to lifecycle"))

            return
        }

        val captureDirectory = File(context.cacheDir, "camera_captures").apply {
            mkdirs()
        }

        val photoFile = File.createTempFile("capture_", ".jpg", captureDirectory)

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(photoFile)
            .build()

        controller.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)

                    onSuccess(uri)
                }

                override fun onError(exception: ImageCaptureException) {
                    photoFile.delete()

                    onError(exception)
                }
            }
        )
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
