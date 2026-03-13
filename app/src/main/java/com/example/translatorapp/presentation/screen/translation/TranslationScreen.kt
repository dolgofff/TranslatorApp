package com.example.translatorapp.presentation.screen.translation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translatorapp.presentation.screen.translation.account.AccountBottomSheet
import com.example.translatorapp.presentation.screen.translation.account.AccountViewModel
import com.example.translatorapp.presentation.ui.components.BottomActionsBar
import com.example.translatorapp.presentation.ui.components.LanguageSelector
import com.example.translatorapp.presentation.ui.components.TranslationCard
import com.example.translatorapp.presentation.ui.components.TranslationTopBar

@Composable
fun TranslationScreen(
    accountViewModel: AccountViewModel = hiltViewModel(),
    onHistoryNavClick: () -> Unit,
    onFavouritesNavClick: () -> Unit,
) {
    val userState by accountViewModel.userState.collectAsStateWithLifecycle()

    var isSheetVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TranslationTopBar(
                onFavouritesClick = onFavouritesNavClick,
                onAccountClick = { isSheetVisible = true }
            )
        },
        bottomBar = { BottomActionsBar(onHistoryClick = onHistoryNavClick) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(16.dp))

            TranslationCard(
                sourceText = "",
                translatedText = "",
                hasInput = false,
                onTranslationTextChanged = {},
                isFocused = false,
                onFocusChanged = {},
                onPaste = {}
            )

            Spacer(Modifier.height(16.dp))

            LanguageSelector(
                sourceLanguage = "English",
                targetLanguage = "Russian"
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