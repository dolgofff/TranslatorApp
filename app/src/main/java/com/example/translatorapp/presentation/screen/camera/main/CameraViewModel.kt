package com.example.translatorapp.presentation.screen.camera.main

import android.net.Uri
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translatorapp.data.media.camera.CameraController
import com.example.translatorapp.domain.media.camera.CameraTextTranslator
import com.example.translatorapp.domain.media.camera.TextStabilizer
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.ml.DisplayedTextBlock
import com.example.translatorapp.domain.model.ml.RecognizedText
import com.example.translatorapp.domain.usecase.dataStore.ObservePreferencesUseCase
import com.example.translatorapp.domain.usecase.dataStore.SetDestinationLanguageUseCase
import com.example.translatorapp.domain.usecase.dataStore.SetSourceLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraController: CameraController,
    private val setSourceLanguageUseCase: SetSourceLanguageUseCase,
    private val setDestinationLanguageUseCase: SetDestinationLanguageUseCase,
    private val cameraTextTranslator: CameraTextTranslator,
    private val observePreferencesUseCase: ObservePreferencesUseCase,
    private val textStabilizer: TextStabilizer,
) : ViewModel() {
    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState = _cameraState.asStateFlow()

    private val _events = MutableSharedFlow<CameraEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private val stabilizedRecognizedText = cameraController.recognizedText
        .filterNotNull()
        .map { recognized -> textStabilizer.stabilize(recognized) }
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            replay = 1
        )

    init {
        observeSavedLanguages()
        observeRecognizedText()
        observeTranslations()
    }

    fun startCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
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

    fun onCaptureClick() {
        if (_cameraState.value.isCapturing)
            return

        _cameraState.update { it.copy(isCapturing = true) }

        cameraController.takePicture(
            onSuccess = { uri ->
                _cameraState.update { it.copy(isCapturing = false) }

                _events.tryEmit(CameraEvent.ImageCaptured(uri))
            },

            onError = { throwable ->
                Log.e("CameraViewModel", "Image capture failed", throwable)

                _cameraState.update { it.copy(isCapturing = false) }

                _events.tryEmit(
                    CameraEvent.CaptureFailed(
                        message = throwable.message ?: "Failed to capture image"
                    )
                )
            }
        )
    }

    private fun observeTranslations() {
        stabilizedRecognizedText
            .map { recognized ->
                val state = _cameraState.value

                TranslationRequest(
                    recognizedText = recognized,
                    textKey = recognized.blocks
                        .joinToString("\n") { it.text.trim() }
                        .trim(),
                    sourceLanguage = state.sourceLanguage,
                    destinationLanguage = state.destinationLanguage
                )
            }
            .filter { it.textKey.isNotBlank() }
            .distinctUntilChanged { old, new ->
                old.textKey == new.textKey &&
                        old.sourceLanguage == new.sourceLanguage &&
                        old.destinationLanguage == new.destinationLanguage
            }
            .mapLatest { request ->
                delay(700)

                cameraTextTranslator.translate(
                    recognizedText = request.recognizedText,
                    sourceLanguage = request.sourceLanguage,
                    destinationLanguage = request.destinationLanguage
                )
            }
            .onEach { refreshDisplayedBlocks() }
            .launchIn(viewModelScope)
    }

    private fun observeRecognizedText() {
        stabilizedRecognizedText
            .onEach { recognized ->
                val state = _cameraState.value

                val displayedBlocks = cameraTextTranslator.getDisplayedBlocks(
                    recognizedText = recognized,
                    sourceLanguage = state.sourceLanguage,
                    destinationLanguage = state.destinationLanguage
                )

                _cameraState.update {
                    it.copy(recognizedText = recognized, translatedBlocks = displayedBlocks)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeSavedLanguages() {
        viewModelScope.launch {
            val settings = observePreferencesUseCase().first()

            val savedSourceLanguage = settings.savedSourceLanguage
            val savedDestinationLanguage = settings.savedDestinationLanguage

            _cameraState.update { current ->
                if (
                    current.sourceLanguage == savedSourceLanguage &&
                    current.destinationLanguage == savedDestinationLanguage
                ) {
                    current
                } else {
                    current.copy(
                        sourceLanguage = savedSourceLanguage,
                        destinationLanguage = savedDestinationLanguage
                    )
                }
            }
        }
    }

    fun updateSourceLanguage(language: LanguageCode) {
        _cameraState.update { it.copy(sourceLanguage = language) }

        viewModelScope.launch {
            setSourceLanguageUseCase(language.code)

            cameraTextTranslator.clearCache()
            refreshDisplayedBlocks()
        }
    }

    fun updateDestinationLanguage(language: LanguageCode) {
        _cameraState.update { it.copy(destinationLanguage = language) }

        viewModelScope.launch {
            setDestinationLanguageUseCase(language.code)

            cameraTextTranslator.clearCache()
            refreshDisplayedBlocks()
        }
    }

    fun onSwapLanguages() {
        val newSourceLanguage = _cameraState.value.destinationLanguage
        val newDestinationLanguage = _cameraState.value.sourceLanguage

        _cameraState.update { cur ->
            cur.copy(
                sourceLanguage = newSourceLanguage,
                destinationLanguage = newDestinationLanguage
            )
        }

        viewModelScope.launch {
            setSourceLanguageUseCase(newSourceLanguage.code)
            setDestinationLanguageUseCase(newDestinationLanguage.code)

            cameraTextTranslator.clearCache()
            refreshDisplayedBlocks()
        }
    }

    private fun refreshDisplayedBlocks() {
        val state = _cameraState.value
        val recognized = state.recognizedText ?: return

        val displayedBlocks = cameraTextTranslator.getDisplayedBlocks(
            recognizedText = recognized,
            sourceLanguage = state.sourceLanguage,
            destinationLanguage = state.destinationLanguage
        )

        _cameraState.update { it.copy(translatedBlocks = displayedBlocks) }
    }

    sealed interface CameraEvent {
        data class ImageCaptured(val uri: Uri) : CameraEvent
        data class CaptureFailed(val message: String) : CameraEvent
    }

    private data class TranslationRequest(
        val recognizedText: RecognizedText,
        val textKey: String,
        val sourceLanguage: LanguageCode,
        val destinationLanguage: LanguageCode,
    )

    data class CameraState(
        val recognizedText: RecognizedText? = null,
        val translatedBlocks: List<DisplayedTextBlock> = emptyList(),
        val isTorchOn: Boolean = false,
        val isCapturing: Boolean = false,
        val sourceLanguage: LanguageCode = LanguageCode.ENGLISH,
        val destinationLanguage: LanguageCode = LanguageCode.RUSSIAN,
        val languageList: List<LanguageCode> = LanguageCode.Companion.getLanguageList(),
    )
}