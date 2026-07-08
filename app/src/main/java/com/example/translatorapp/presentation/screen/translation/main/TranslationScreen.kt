package com.example.translatorapp.presentation.screen.translation.main

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.presentation.common.rememberPermissionHandler
import com.example.translatorapp.presentation.screen.translation.account.AccountBottomSheet
import com.example.translatorapp.presentation.screen.translation.account.AccountViewModel
import com.example.translatorapp.presentation.ui.components.BottomActionsBar
import com.example.translatorapp.presentation.ui.components.LanguageSelector
import com.example.translatorapp.presentation.ui.components.TranslationCard
import com.example.translatorapp.presentation.ui.components.TranslationTopBar
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun TranslationScreen(
    accountViewModel: AccountViewModel = hiltViewModel(),
    translationViewModel: TranslationViewModel = hiltViewModel(),
    onHistoryNavClick: () -> Unit,
    onFavouritesNavClick: () -> Unit,
) {
    val userState by accountViewModel.userState.collectAsStateWithLifecycle()
    val translationState by translationViewModel.translationState.collectAsStateWithLifecycle()

    var isSheetVisible by rememberSaveable { mutableStateOf(false) }
    val uiMode = translationState.uiMode

    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    val requestRecordAudioPermission = rememberPermissionHandler(
        permission = Manifest.permission.RECORD_AUDIO,
        onPermissionGranted = translationViewModel::startVoiceRecognition
    )

    LaunchedEffect(translationState.errorMessage) {
        translationState.errorMessage?.let { snackbarHostState.showSnackbar(message = it) }

        translationViewModel.clearErrorMessage()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TranslationTopBar(
                onFavouritesClick = onFavouritesNavClick,
                isSimple = uiMode != TranslationUiMode.IDLE,
                onResetUiMode = {
                    if (uiMode == TranslationUiMode.EDITING) {
                        focusManager.clearFocus()
                    }

                    translationViewModel.reset()
                },
                onAccountClick = { isSheetVisible = true }
            )
        },
        bottomBar = {
            if (uiMode == TranslationUiMode.IDLE) {
                BottomActionsBar(
                    onHistoryClick = onHistoryNavClick,
                    isRecording = translationState.isRecording,
                    onAudioButtonClick = {
                        if (translationState.isRecording)
                            translationViewModel.stopVoiceRecognition()
                        else
                            requestRecordAudioPermission()
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets.ime,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .imePadding()
        ) {
            Spacer(Modifier.height(16.dp))

            TranslationCard(
                sourceText = translationState.sourceText,
                translatedText = translationState.translatedText,
                hasInput = translationState.hasInput,
                onPlaySourceAudio = translationViewModel::playSourceAudio,
                onPlayTranslationAudio = translationViewModel::playTranslatedAudio,
                isFocused = uiMode == TranslationUiMode.EDITING,
                onFocusChanged = translationViewModel::onFocusChanged,
                onTextChanged = translationViewModel::updateSourceText,
                isFavourite = translationState.isFavourite,
                onToggleFavourite = translationViewModel::toggleFavourite,
                onDone = {
                    translationViewModel.onDone()
                    focusManager.clearFocus()
                }
            )

            Spacer(Modifier.height(16.dp))

            LanguageSelector(
                sourceLanguage = translationState.sourceLanguage,
                destinationLanguage = translationState.destinationLanguage,
                languageList = translationState.languageList,
                onSourceLanguageChange = translationViewModel::updateSourceLanguage,
                onDestinationLanguageChange = translationViewModel::updateDestinationLanguage,
                onSwapLanguages = translationViewModel::onSwapLanguages
            )
        }
    }

    if (isSheetVisible) {
        AccountBottomSheet(
            photoUrl = userState.photoUrl,
            name = userState.name,
            email = userState.email,
            onDismissRequest = { isSheetVisible = false },
            onSettingsClick = { isSheetVisible = false }, //TODO: Implement SettingScreen
            onLogoutClick = {
                isSheetVisible = false
                accountViewModel.logout()
            }
        )
    }
}