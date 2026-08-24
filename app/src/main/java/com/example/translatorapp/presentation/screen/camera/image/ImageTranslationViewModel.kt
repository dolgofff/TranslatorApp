package com.example.translatorapp.presentation.screen.camera.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translatorapp.domain.media.camera.CameraTextTranslator
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.ml.DisplayedTextBlock
import com.example.translatorapp.domain.model.ml.RecognizedText
import com.example.translatorapp.domain.usecase.dataStore.ObservePreferencesUseCase
import com.example.translatorapp.domain.usecase.dataStore.SetDestinationLanguageUseCase
import com.example.translatorapp.domain.usecase.dataStore.SetSourceLanguageUseCase
import com.example.translatorapp.domain.usecase.dataStore.SwapLanguagesUseCase
import com.example.translatorapp.domain.usecase.translation.RecognizeImageTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageTranslationViewModel @Inject constructor(
    private val recognizeImageTextUseCase: RecognizeImageTextUseCase,
    private val cameraTextTranslator: CameraTextTranslator,
    private val setSourceLanguageUseCase: SetSourceLanguageUseCase,
    private val setDestinationLanguageUseCase: SetDestinationLanguageUseCase,
    private val swapLanguagesUseCase: SwapLanguagesUseCase,
    private val observePreferencesUseCase: ObservePreferencesUseCase,
) : ViewModel() {
    private val _recognitionState = MutableStateFlow(ImageTranslationState())
    val recognitionState = _recognitionState.asStateFlow()

    private var recognitionJob: Job? = null
    private var translationJob: Job? = null

    init {
        observeSavedLanguages()
    }

    fun loadImage(uri: String) {
        val currentState = _recognitionState.value

        val alreadyLoaded = currentState.imageUri == uri && currentState.recognizedText != null
        val currentlyLoading = currentState.imageUri == uri && currentState.isLoading

        if (alreadyLoaded || currentlyLoading)
            return

        recognitionJob?.cancel()
        translationJob?.cancel()

        recognitionJob = viewModelScope.launch {
            _recognitionState.update {
                it.copy(
                    imageUri = uri,
                    imageWidth = 0,
                    imageHeight = 0,
                    recognizedText = null,
                    translatedBlocks = emptyList(),
                    isLoading = true,
                    errorMessage = null
                )
            }

            recognizeCurrentImage()
        }
    }

    fun updateSourceLanguage(language: LanguageCode) {
        if (_recognitionState.value.sourceLanguage == language)
            return

        _recognitionState.update {
            it.copy(
                sourceLanguage = language,
                recognizedText = null,
                translatedBlocks = emptyList(),
                errorMessage = null
            )
        }

        viewModelScope.launch { setSourceLanguageUseCase(language.code) }

        retryImageRecognition()
    }

    fun updateDestinationLanguage(language: LanguageCode) {
        if (_recognitionState.value.destinationLanguage == language)
            return

        _recognitionState.update {
            it.copy(
                destinationLanguage = language,
                translatedBlocks = emptyList(),
                errorMessage = null
            )
        }

        viewModelScope.launch { setDestinationLanguageUseCase(language.code) }

        retranslate(clearCache = true)
    }

    fun onSwapLanguages() {
        val newSource = _recognitionState.value.destinationLanguage
        val newDestination = _recognitionState.value.sourceLanguage

        _recognitionState.update {
            it.copy(
                sourceLanguage = newSource,
                destinationLanguage = newDestination,
                recognizedText = null,
                translatedBlocks = emptyList(),
                errorMessage = null
            )
        }

        viewModelScope.launch {
            swapLanguagesUseCase(
                sourceCode = newSource.code,
                destinationCode = newDestination.code
            )
        }

        retryImageRecognition()
    }

    fun getSourceText(): String = _recognitionState.value.recognizedText?.blocks
        ?.joinToString("\n") { block -> block.text.trim() }
        ?.trim()
        .orEmpty()

    private fun observeSavedLanguages() {
        viewModelScope.launch {
            observePreferencesUseCase().collect { settings ->
                val currentState = _recognitionState.value

                val sourceChanged = currentState.sourceLanguage != settings.savedSourceLanguage
                val destinationChanged =
                    currentState.destinationLanguage != settings.savedDestinationLanguage

                if (!sourceChanged && !destinationChanged)
                    return@collect

                _recognitionState.update {
                    it.copy(
                        sourceLanguage = settings.savedSourceLanguage,
                        destinationLanguage = settings.savedDestinationLanguage
                    )
                }

                when {
                    sourceChanged -> retryImageRecognition()
                    destinationChanged -> retranslate(clearCache = true)
                }
            }
        }
    }

    private fun retryImageRecognition() {
        if (_recognitionState.value.imageUri == null)
            return

        recognitionJob?.cancel()
        translationJob?.cancel()

        recognitionJob = viewModelScope.launch {
            cameraTextTranslator.clearCache()

            _recognitionState.update {
                it.copy(
                    isLoading = true,
                    recognizedText = null,
                    translatedBlocks = emptyList(),
                    errorMessage = null
                )
            }

            recognizeCurrentImage()
        }
    }

    private suspend fun recognizeCurrentImage() {
        val state = _recognitionState.value

        val sourceLanguage = state.sourceLanguage
        val imageUri = state.imageUri ?: return

        recognizeImageTextUseCase(
            imageUri = imageUri,
            sourceLanguage = sourceLanguage
        )
            .onSuccess { result ->
                _recognitionState.update {
                    it.copy(
                        imageWidth = result.width,
                        imageHeight = result.height,
                        recognizedText = result.recognizedText,
                        translatedBlocks = emptyList(),
                        isLoading = false,
                        errorMessage = null
                    )
                }

                retranslate(clearCache = true)
            }
            .onFailure { throwable ->
                _recognitionState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to recognize text"
                    )
                }
            }
    }

    private fun retranslate(clearCache: Boolean) {
        translationJob?.cancel()

        translationJob = viewModelScope.launch {
            if (clearCache)
                cameraTextTranslator.clearCache()

            translateRecognizedText()
        }
    }

    private suspend fun translateRecognizedText() {
        val state = _recognitionState.value
        val recognized = state.recognizedText ?: return

        cameraTextTranslator.translate(
            recognizedText = recognized,
            sourceLanguage = state.sourceLanguage,
            destinationLanguage = state.destinationLanguage
        )

        refreshDisplayedBlocks()
    }

    private fun refreshDisplayedBlocks() {
        val state = _recognitionState.value
        val recognized = state.recognizedText ?: return

        val blocks = cameraTextTranslator.getDisplayedBlocks(
            recognizedText = recognized,
            sourceLanguage = state.sourceLanguage,
            destinationLanguage = state.destinationLanguage
        )

        _recognitionState.update { it.copy(translatedBlocks = blocks) }
    }

    data class ImageTranslationState(
        val imageUri: String? = null,
        val imageWidth: Int = 0,
        val imageHeight: Int = 0,
        val recognizedText: RecognizedText? = null,
        val translatedBlocks: List<DisplayedTextBlock> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val sourceLanguage: LanguageCode = LanguageCode.ENGLISH,
        val destinationLanguage: LanguageCode = LanguageCode.RUSSIAN,
        val languageList: List<LanguageCode> = LanguageCode.getLanguageList(),
    )
}