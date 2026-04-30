package com.example.translatorapp.presentation.screen.translation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translatorapp.domain.model.LanguageCode
import com.example.translatorapp.domain.model.Translation
import com.example.translatorapp.domain.usecase.audio.PlayAudioUseCase
import com.example.translatorapp.domain.usecase.dataStore.ObservePreferencesUseCase
import com.example.translatorapp.domain.usecase.dataStore.SetDestinationLanguageUseCase
import com.example.translatorapp.domain.usecase.dataStore.SetSourceLanguageUseCase
import com.example.translatorapp.domain.usecase.translation.SaveTranslationUseCase
import com.example.translatorapp.domain.usecase.translation.ToggleFavouriteUseCase
import com.example.translatorapp.domain.usecase.translation.TranslateTextUseCase
import com.example.translatorapp.presentation.mapper.toUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@FlowPreview
@HiltViewModel
class TranslationViewModel @Inject constructor(
    private val translateTextUseCase: TranslateTextUseCase,
    private val saveTranslationUseCase: SaveTranslationUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    private val playAudioUseCase: PlayAudioUseCase,
    private val setSourceLanguageUseCase: SetSourceLanguageUseCase,
    private val setDestinationLanguageUseCase: SetDestinationLanguageUseCase,
    observePreferencesUseCase: ObservePreferencesUseCase,
) : ViewModel() {
    private val _translationState = MutableStateFlow(TranslationState())
    val translationState = _translationState.asStateFlow()

    private val debouncedParams = _translationState
        .map { Triple(it.sourceText, it.sourceLanguage, it.destinationLanguage) }
        .debounce(500)
        .distinctUntilChanged()

    init {
        viewModelScope.launch {
            val settings = observePreferencesUseCase().first()
            val savedSourceLanguage = settings.savedSourceLanguage
            val savedDestinationLanguage = settings.savedDestinationLanguage

            _translationState.update { cur ->
                if (cur.sourceLanguage == savedSourceLanguage && cur.destinationLanguage == savedDestinationLanguage) {
                    return@update cur
                }

                cur.copy(
                    sourceLanguage = savedSourceLanguage,
                    destinationLanguage = savedDestinationLanguage
                )
            }
        }

        viewModelScope.launch {
            debouncedParams
                .filter { it.first.isNotBlank() }
                .mapLatest { (text, sourceLanguage, destinationLanguage) ->
                    translateTextUseCase(
                        text = text,
                        sourceLanguage = sourceLanguage,
                        destinationLanguage = destinationLanguage,
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { translation ->
                            _translationState.update {
                                it.copy(
                                    translatedText = translation.translatedText,
                                    sourceTextAudio = translation.sourceAudioUrl,
                                    translatedTextAudio = translation.destinationAudioUrl,
                                    isFavourite = translation.isFavourite,
                                    errorMessage = null,
                                    translationSnapshot = translation
                                )
                            }
                        },
                        onFailure = { error ->
                            _translationState.update { it.copy(errorMessage = error.toUiMessage()) }
                        }
                    )
                }
        }
    }

    fun updateSourceText(text: String) {
        _translationState.update { it.copy(sourceText = text) }
    }

    fun updateSourceLanguage(language: LanguageCode) {
        _translationState.update { it.copy(sourceLanguage = language) }

        viewModelScope.launch { setSourceLanguageUseCase(language.code) }
    }

    fun updateDestinationLanguage(language: LanguageCode) {
        _translationState.update { it.copy(destinationLanguage = language) }

        viewModelScope.launch { setDestinationLanguageUseCase(language.code) }
    }

    fun onSwapLanguages() {
        val newSourceLanguage = _translationState.value.destinationLanguage
        val newDestinationLanguage = _translationState.value.sourceLanguage

        _translationState.update { cur ->
            cur.copy(
                sourceLanguage = newSourceLanguage,
                destinationLanguage = newDestinationLanguage
            )
        }

        viewModelScope.launch {
            setSourceLanguageUseCase(newSourceLanguage.code)
            setDestinationLanguageUseCase(newDestinationLanguage.code)
        }
    }

    fun onFocusChanged(focused: Boolean) {
        _translationState.update {
            it.copy(
                uiMode = if (focused) {
                    TranslationUiMode.EDITING
                } else {
                    it.uiMode
                }
            )
        }
    }

    fun onDone() {
        _translationState.update {
            it.copy(uiMode = TranslationUiMode.RESULT)
        }

        saveTranslation()
    }

    fun reset() {
        _translationState.update {
            it.copy(
                sourceText = "",
                translatedText = "",
                sourceTextAudio = null,
                translatedTextAudio = null,
                isFavourite = false,
                translationSnapshot = null,
                uiMode = TranslationUiMode.IDLE
            )
        }
    }

    fun playSourceAudio() {
        val sourceTextAudio = _translationState.value.sourceTextAudio

        if (sourceTextAudio.isNullOrBlank()) return

        viewModelScope.launch {
            playAudioUseCase(sourceTextAudio)
        }
    }

    fun playTranslatedAudio() {
        val translatedTextAudio = _translationState.value.translatedTextAudio

        if (translatedTextAudio.isNullOrBlank()) return

        viewModelScope.launch {
            playAudioUseCase(translatedTextAudio)
        }
    }

    fun toggleFavourite() {
        val translation = _translationState.value.translationSnapshot ?: return
        val updatedValue = !translation.isFavourite

        viewModelScope.launch {
            val result =
                toggleFavouriteUseCase(id = translation.id, isFavourite = updatedValue)

            result.fold(
                onSuccess = {
                    _translationState.update {
                        it.copy(isFavourite = updatedValue)
                    }
                },
                onFailure = { error ->
                    _translationState.update { it.copy(errorMessage = error.toUiMessage()) }
                }
            )
        }
    }

    fun saveTranslation() {
        val currentState = _translationState.value

        if (currentState.sourceText.isBlank() || currentState.translatedText.isBlank()) return

        currentState.translationSnapshot?.let { translation ->
            viewModelScope.launch {
                val result = saveTranslationUseCase(translation = translation)

                result.onFailure { error ->
                    _translationState.update { it.copy(errorMessage = error.toUiMessage()) }
                }
            }
        }
    }

    fun clearErrorMessage() {
        _translationState.update { it.copy(errorMessage = null) }
    }

    data class TranslationState(
        val sourceText: String = "",
        val translatedText: String = "",
        val sourceLanguage: LanguageCode = LanguageCode.ENGLISH,
        val destinationLanguage: LanguageCode = LanguageCode.RUSSIAN,
        val sourceTextAudio: String? = null,
        val translatedTextAudio: String? = null,
        val isFavourite: Boolean = false,
        val languageList: List<LanguageCode> = LanguageCode.getLanguageList(),
        val errorMessage: String? = null,
        val translationSnapshot: Translation? = null,
        val uiMode: TranslationUiMode = TranslationUiMode.IDLE,
    ) {
        val hasInput: Boolean
            get() = sourceText.isNotBlank()
    }
}