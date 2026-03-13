package com.example.translatorapp.presentation.screen.favourites

import com.example.translatorapp.domain.model.Translation

sealed class FavouritesUIState {
    data class Error(val errorMessage: String) : FavouritesUIState()
    data class ShowContent(val translationsList: List<Translation>) : FavouritesUIState()
}