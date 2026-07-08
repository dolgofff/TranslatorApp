package com.example.translatorapp.presentation.screen.camera

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.translatorapp.data.media.camera.CameraController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val cameraController: CameraController) :
    ViewModel() {
    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState = _cameraState.asStateFlow()

    suspend fun startCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        cameraController.bind(lifecycleOwner, previewView)
    }

    fun stopCamera() {
        cameraController.unbind()
    }

    fun onFlashClick() {
        val newState = !_cameraState.value.isTorchOn

        _cameraState.update { it.copy(isTorchOn = newState) }

        cameraController.setTorch(newState)
    }

    data class CameraState(
        val isTorchOn: Boolean = false,
    )
}