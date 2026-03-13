package com.example.translatorapp.presentation.screen.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translatorapp.domain.usecase.translation.ObserveFavouritesUseCase
import com.example.translatorapp.domain.usecase.translation.ToggleFavouriteUseCase
import com.example.translatorapp.presentation.mapper.toUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    observeFavouritesUseCase: ObserveFavouritesUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
) : ViewModel() {
    private val sortType = MutableStateFlow(SortType.BY_DATE)

    @OptIn(ExperimentalTime::class)
    val favouritesState: StateFlow<FavouritesUIState> =
        combine(flow = observeFavouritesUseCase(), sortType) { result, sortType ->
            result.fold(
                onSuccess = { list ->
                    val sortedList = when (sortType) {
                        SortType.BY_ALPHABET -> list.sortedBy { it.sourceText.lowercase() }
                        SortType.BY_DATE -> list.sortedByDescending { it.timestamp }
                    }

                    FavouritesUIState.ShowContent(translationsList = sortedList)
                },
                onFailure = { FavouritesUIState.Error(errorMessage = it.toUiMessage()) }
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = FavouritesUIState.ShowContent(translationsList = emptyList())
        )


    fun toggleFavourite(id: String, isFavourite: Boolean) {
        viewModelScope.launch {
            toggleFavouriteUseCase(id = id, isFavourite = !isFavourite)
        }
    }

    fun sortByAlphabet() {
        sortType.value = SortType.BY_ALPHABET
    }

    fun sortByDate() {
        sortType.value = SortType.BY_DATE
    }

    enum class SortType {
        BY_ALPHABET,
        BY_DATE
    }
}