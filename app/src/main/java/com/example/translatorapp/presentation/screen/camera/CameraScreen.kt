package com.example.translatorapp.presentation.screen.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.presentation.ui.components.CameraBottomBar
import com.example.translatorapp.presentation.ui.components.CameraPreview
import com.example.translatorapp.presentation.ui.components.CameraTopBar
import com.example.translatorapp.presentation.ui.components.TranslationOverlay
import com.example.translatorapp.presentation.ui.components.TransparentLanguageSelector

/*
onGalleryClick: () -> Unit = {},
onCaptureClick: () -> Unit = {},
onSwapLanguages: () -> Unit = {},
*/
@Composable
fun CameraScreen(cameraViewModel: CameraViewModel = hiltViewModel(), onNavBackClick: () -> Unit) {
    val state by cameraViewModel.cameraState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onStartCamera = cameraViewModel::startCamera,
            onStopCamera = cameraViewModel::stopCamera
        )

        TranslationOverlay()

        CameraTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            onNavBackClick = onNavBackClick
        )

        TransparentLanguageSelector(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp),
            sourceLanguage = LanguageCode.ENGLISH,
            targetLanguage = LanguageCode.RUSSIAN,
            onSwapLanguages = {},
            languageList = emptyList(),
            onSourceLanguageChange = {},
            onTargetLanguageChange = {}
        )

        CameraBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onGalleryClick = {},
            onCaptureClick = {},
            onFlashClick = cameraViewModel::onFlashClick,
            isTorchOn = state.isTorchOn
        )
    }
}