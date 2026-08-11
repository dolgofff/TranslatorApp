package com.example.translatorapp.presentation.screen.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.presentation.common.rememberPermissionHandler
import com.example.translatorapp.presentation.ui.components.CameraBottomBar
import com.example.translatorapp.presentation.ui.components.CameraOverlay
import com.example.translatorapp.presentation.ui.components.CameraPreview
import com.example.translatorapp.presentation.ui.components.CameraTopBar
import com.example.translatorapp.presentation.ui.components.TransparentLanguageSelector

@Composable
fun CameraScreen(cameraViewModel: CameraViewModel = hiltViewModel(), onNavBackClick: () -> Unit) {
    val state by cameraViewModel.cameraState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestCameraPermission = rememberPermissionHandler(
        permission = Manifest.permission.CAMERA,
        onPermissionGranted = { hasCameraPermission = true }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission)
            requestCameraPermission()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                context = context,
                onStartCamera = cameraViewModel::startCamera,
                onStopCamera = cameraViewModel::stopCamera
            )
        }

        CameraOverlay(
            blocks = state.translatedBlocks,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            CameraTopBar(onNavBackClick = onNavBackClick)

            TransparentLanguageSelector(
                sourceLanguage = state.sourceLanguage,
                destinationLanguage = state.destinationLanguage,
                onSwapLanguages = cameraViewModel::onSwapLanguages,
                languageList = state.languageList,
                onSourceLanguageChange = cameraViewModel::updateSourceLanguage,
                onDestinationLanguageChange = cameraViewModel::updateDestinationLanguage
            )
        }

        CameraBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onGalleryClick = {},
            onCaptureClick = {},
            onFlashClick = cameraViewModel::onFlashClick,
            isTorchOn = state.isTorchOn
        )
    }
}