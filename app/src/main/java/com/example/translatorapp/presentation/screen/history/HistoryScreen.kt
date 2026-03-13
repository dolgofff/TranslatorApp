package com.example.translatorapp.presentation.screen.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.presentation.ui.components.ErrorView
import com.example.translatorapp.presentation.ui.components.HistoryList
import com.example.translatorapp.presentation.ui.components.HistoryTopBar

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel(), onNavBackClick: () -> Unit) {
    val state by viewModel.historyState.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        HistoryTopBar(
            onNavBackClick = onNavBackClick,
            onClear = viewModel::clear
        )
    }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = state) {
                is HistoryUIState.ShowContent -> HistoryList(
                    translationsList = s.translationsList,
                    onDelete = viewModel::delete,
                    onToggleFavourite = viewModel::toggleFavourite
                )

                is HistoryUIState.Error -> ErrorView(s.errorMessage)
            }
        }
    }
}


/*@Composable
@Preview(showBackground = true)
fun PreviewHistoryScreen() {
    val ts: (String, Boolean) -> Unit = {a, isValid -> }
    Scaffold(topBar = {
        HistoryTopBar(
            onNavBackClick = { },
            onClear = { }
        )
    }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            @OptIn(ExperimentalTime::class)
            HistoryList(
                translationsList = listOf(
                    Translation(
                        "someId",
                        "Hello",
                        "Привет",
                        "English",
                        "Russian",
                        isFavourite = false,
                        timestamp = Clock.System.now()
                    ),
                    Translation(
                        "someId43",
                        "Hello",
                        "Привет",
                        "English",
                        "Russian",
                        isFavourite = true,
                        timestamp = Clock.System.now()
                    ),
                ),
                onDelete = { },
                onToggleFavourite = ts
            )

            //ErrorView("You don't have any history yet.")

        }
    }
}*/
