package com.example.translatorapp.presentation.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translatorapp.domain.usecase.translation.ClearHistoryUseCase
import com.example.translatorapp.domain.usecase.translation.DeleteTranslationUseCase
import com.example.translatorapp.domain.usecase.translation.ObserveTranslationsUseCase
import com.example.translatorapp.domain.usecase.translation.ToggleFavouriteUseCase
import com.example.translatorapp.presentation.mapper.toUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    observeTranslationsUseCase: ObserveTranslationsUseCase,
    private val deleteTranslationUseCase: DeleteTranslationUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
) : ViewModel() {
    val historyState: StateFlow<HistoryUIState> = observeTranslationsUseCase()
        .map { result ->
            result.fold(
                onSuccess = { HistoryUIState.ShowContent(translationsList = it) },
                onFailure = { HistoryUIState.Error(errorMessage = it.toUiMessage()) }
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = HistoryUIState.ShowContent(translationsList = emptyList())
        )

    fun delete(id: String) {
        viewModelScope.launch {
            deleteTranslationUseCase(id)
        }
    }

    fun clear() {
        viewModelScope.launch {
            clearHistoryUseCase()
        }
    }

    fun toggleFavourite(id: String, isFavourite: Boolean) {
        viewModelScope.launch {
            toggleFavouriteUseCase(id = id, isFavourite = !isFavourite)
        }
    }
}