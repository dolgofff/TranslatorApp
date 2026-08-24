package com.example.translatorapp.presentation.screen.history

import com.example.translatorapp.domain.model.base.Translation

sealed class HistoryUIState {
    data class ShowContent(val translationsList: List<Translation>) : HistoryUIState()
    data class Error(val errorMessage: String) : HistoryUIState()
}