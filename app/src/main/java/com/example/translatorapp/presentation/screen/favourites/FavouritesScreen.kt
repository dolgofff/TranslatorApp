package com.example.translatorapp.presentation.screen.favourites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.presentation.ui.components.ErrorView
import com.example.translatorapp.presentation.ui.components.FavouritesList
import com.example.translatorapp.presentation.ui.components.FavouritesTopBar

@Composable
fun FavouritesScreen(viewmodel: FavouritesViewModel = hiltViewModel(), onNavBackClick: () -> Unit) {
    val state by viewmodel.favouritesState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            FavouritesTopBar(
                onNavBackClick = onNavBackClick,
                onSortByAlphabet = viewmodel::sortByAlphabet,
                onSortByDate = viewmodel::sortByDate
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = state) {
                is FavouritesUIState.ShowContent -> FavouritesList(
                    translationsList = s.translationsList,
                    onToggleFavourite = viewmodel::toggleFavourite
                )

                is FavouritesUIState.Error -> ErrorView(s.errorMessage)
            }
        }
    }
}

/*
@Composable
@Preview(showBackground = true)
@OptIn(kotlin.time.ExperimentalTime::class)
fun PreviewFavouritesScreen() {
    val ts: (String, Boolean) -> Unit = { a, isValid -> }
    Scaffold(topBar = {
        FavouritesTopBar(
            onNavBackClick = { },
            onSortByAlphabet = {},
            onSortByDate = {}
        )
    }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            FavouritesList(
                translationsList = listOf(
                    Translation(
                        "someId3",
                        "Hello",
                        "Привет",
                        "English",
                        "Russian",
                        isFavourite = true,
                        timestamp = Clock.System.now()
                    ),
                    Translation(
                        "someId4",
                        "Hello",
                        "Привет",
                        "English",
                        "Russian",
                        isFavourite = false,
                        timestamp = Clock.System.now()
                    )
                ),
                onToggleFavourite = ts
            )

            //ErrorView(s.errorMessage)

        }
    }
}*/
