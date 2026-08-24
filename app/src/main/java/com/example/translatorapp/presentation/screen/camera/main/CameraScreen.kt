package com.example.translatorapp.presentation.screen.camera.main

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.presentation.common.rememberPermissionHandler
import com.example.translatorapp.presentation.screen.camera.main.CameraViewModel.CameraEvent
import com.example.translatorapp.presentation.ui.components.CameraBottomBar
import com.example.translatorapp.presentation.ui.components.CameraOverlay
import com.example.translatorapp.presentation.ui.components.CameraPreview
import com.example.translatorapp.presentation.ui.components.CameraTopBar
import com.example.translatorapp.presentation.ui.components.TransparentLanguageSelector

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel(),
    onNavBackClick: () -> Unit,
    onImageSelected: (Uri) -> Unit,
) {
    val state by viewModel.cameraState.collectAsStateWithLifecycle()
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

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null)
            onImageSelected(uri)
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission)
            requestCameraPermission()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CameraEvent.ImageCaptured -> {
                    onImageSelected(event.uri)
                }

                is CameraEvent.CaptureFailed -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                context = context,
                onStartCamera = viewModel::startCamera,
                onStopCamera = viewModel::stopCamera
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

            Spacer(modifier = Modifier.height(12.dp))

            TransparentLanguageSelector(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                sourceLanguage = state.sourceLanguage,
                destinationLanguage = state.destinationLanguage,
                onSwapLanguages = viewModel::onSwapLanguages,
                languageList = state.languageList,
                onSourceLanguageChange = viewModel::updateSourceLanguage,
                onDestinationLanguageChange = viewModel::updateDestinationLanguage
            )
        }

        CameraBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onCaptureClick = viewModel::onCaptureClick,
            onFlashClick = viewModel::onFlashClick,
            onGalleryClick = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            isTorchOn = state.isTorchOn,
            isCapturing = state.isCapturing,
        )
    }
}