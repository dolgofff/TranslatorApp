package com.example.translatorapp.presentation.screen.camera.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.presentation.ui.components.ImageTranslationContent

@Composable
fun ImageTranslationScreen(
    imageUri: String,
    onNavBackClick: () -> Unit,
    onGoToTranslatorClick: (String) -> Unit,
    viewModel: ImageTranslationViewModel = hiltViewModel(),
) {
    val state by viewModel.recognitionState.collectAsStateWithLifecycle()

    LaunchedEffect(imageUri) {
        viewModel.loadImage(imageUri)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        ImageTranslationContent(
            state = state,
            onNavBackClick = onNavBackClick,
            onSourceLanguageChange = viewModel::updateSourceLanguage,
            onDestinationLanguageChange = viewModel::updateDestinationLanguage,
            onSwapLanguages = viewModel::onSwapLanguages,
            onGoToTranslatorClick = {
                val sourceText = viewModel.getSourceText()

                if (sourceText.isNotBlank())
                    onGoToTranslatorClick(sourceText)
            }
        )
    }
}