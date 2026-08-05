package com.example.translatorapp.presentation.screen.camera

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translatorapp.data.media.camera.CameraController
import com.example.translatorapp.domain.media.CameraTextTranslator
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.ml.DisplayedTextBlock
import com.example.translatorapp.domain.model.ml.RecognizedText
import com.example.translatorapp.domain.usecase.dataStore.ObservePreferencesUseCase
import com.example.translatorapp.domain.usecase.dataStore.SetDestinationLanguageUseCase
import com.example.translatorapp.domain.usecase.dataStore.SetSourceLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO: Пользователь нажимает на capture, и тогда появляется кнопка "Go to translator" и распознанный текст переносится на экран назад в поле "sourceText"
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraController: CameraController,
    private val setSourceLanguageUseCase: SetSourceLanguageUseCase,
    private val setDestinationLanguageUseCase: SetDestinationLanguageUseCase,
    private val cameraTextTranslator: CameraTextTranslator,
    observePreferencesUseCase: ObservePreferencesUseCase,
) : ViewModel() {
    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState = _cameraState.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = observePreferencesUseCase().first()
            val savedSourceLanguage = settings.savedSourceLanguage
            val savedDestinationLanguage = settings.savedDestinationLanguage

            _cameraState.update { cur ->
                if (cur.sourceLanguage == savedSourceLanguage && cur.destinationLanguage == savedDestinationLanguage) {
                    return@update cur
                }

                cur.copy(
                    sourceLanguage = savedSourceLanguage,
                    destinationLanguage = savedDestinationLanguage
                )
            }
        }

        cameraController.recognizedText
            .filterNotNull()
            .map { recognized ->
                recognized.blocks.joinToString("\n") { block ->
                    block.text.trim()
                } to recognized
            }
            .distinctUntilChanged { old, new ->
                old.first == new.first
            }
            .map { it.second }
            .mapLatest { recognized ->
                val translatedBlocks = cameraTextTranslator.translate(
                    recognizedText = recognized,
                    sourceLanguage = cameraState.value.sourceLanguage,
                    destinationLanguage = cameraState.value.destinationLanguage
                )

                recognized to translatedBlocks
            }
            .onEach { (recognized, translatedBlocks) ->
                _cameraState.update {
                    it.copy(recognizedText = recognized, translatedBlocks = translatedBlocks)
                }
            }
            .launchIn(viewModelScope)
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

    fun updateSourceLanguage(language: LanguageCode) {
        _cameraState.update { it.copy(sourceLanguage = language) }

        viewModelScope.launch {
            setSourceLanguageUseCase(language.code)

            cameraTextTranslator.clearCache()
        }
    }

    fun updateDestinationLanguage(language: LanguageCode) {
        _cameraState.update { it.copy(destinationLanguage = language) }

        viewModelScope.launch {
            setDestinationLanguageUseCase(language.code)

            cameraTextTranslator.clearCache()
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
        }
    }

    data class CameraState(
        val recognizedText: RecognizedText? = null,
        val translatedBlocks: List<DisplayedTextBlock> = emptyList(),
        val isTorchOn: Boolean = false,
        val sourceLanguage: LanguageCode = LanguageCode.ENGLISH,
        val destinationLanguage: LanguageCode = LanguageCode.RUSSIAN,
        val languageList: List<LanguageCode> = LanguageCode.getLanguageList(),
    )
}

/*
fun onCaptureClick() {

}

fun onGalleryClick() {

}*/
