package com.example.translatorapp.presentation.screen.camera

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

    private val stabilizedRecognizedText = cameraController.recognizedText
        .filterNotNull()
        .map { recognized -> textStabilizer.stabilize(recognized) }
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
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
    /*
    1. Пользователь нажимает на capture
    2. Появляется фотка с переведённым текстом (поверх исходного, как в CameraOverlay). Помимо этого в этом режиме сохраняется аналогичный topappbar, только вместо кнопки назад кнопка крестика, нажимая на которую пользователь попадает назад в камеру, transparentlanguageselector сохраняется, и его возможности можно аналогично применять.
    3. Помимо этого, внизу появляется кнопка "Go to translator", при нажатии на которую распознанный текст переносится на основной экран в поле "sourceText"
    */
    }

    fun onGalleryClick() {
        /*
        1. Всё работает по аналогии с onCaptureClick(), только фотка предварительно выбирается из галлереи.
         */
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
        val sourceLanguage: LanguageCode = LanguageCode.ENGLISH,
        val destinationLanguage: LanguageCode = LanguageCode.RUSSIAN,
        val languageList: List<LanguageCode> = LanguageCode.getLanguageList(),
    )
}
