package com.example.translatorapp.presentation.ui.components

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaActionSound
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.translatorapp.R
import com.example.translatorapp.domain.model.base.Translation
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.presentation.common.formatDateTime
import com.example.translatorapp.presentation.ui.theme.ButtonColor
import com.example.translatorapp.presentation.ui.theme.MainColor
import com.example.translatorapp.presentation.ui.theme.TranslationBoxColor
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime

// Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationTopBar(
    isSimple: Boolean,
    onFavouritesClick: () -> Unit,
    onResetUiMode: () -> Unit,
    onAccountClick: () -> Unit,
) {
    if (isSimple)
        CenterAlignedTopAppBar(
            title = {},
            navigationIcon = { ButtonBack(onNavBackClick = onResetUiMode) }
        )
    else
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Translator",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onFavouritesClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Bookmarks,
                        tint = MainColor,
                        contentDescription = "Favourites"
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onAccountClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        tint = MainColor,
                        modifier = Modifier.size(28.dp),
                        contentDescription = "Account"
                    )
                }
            }
        )
}

@Composable
fun TranslationCard(
    modifier: Modifier = Modifier,
    sourceText: String,
    translatedText: String,
    hasInput: Boolean,
    onPlayTranslationAudio: () -> Unit,
    onPlaySourceAudio: () -> Unit,
    isFocused: Boolean,
    onTextChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onDone: () -> Unit,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
) {
    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val hapticFeedback = LocalHapticFeedback.current

    val hasClipboardText = rememberHasClipboardText()

    val focusRequester = remember { FocusRequester() }
    val elevation by animateDpAsState(targetValue = if (isFocused) 8.dp else 2.dp)

    val textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 30.sp, lineHeight = 36.sp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (!isFocused) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.VirtualKey)

                    focusRequester.requestFocus()
                }
            },
        shape = RoundedCornerShape(24.dp),
        tonalElevation = elevation,
        color = TranslationBoxColor
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .animateContentSize()
        ) {
            OutlinedTextField(
                value = sourceText,
                onValueChange = onTextChanged,
                textStyle = textStyle,
                placeholder = {
                    Text(
                        text = "Enter text",
                        style = textStyle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (hasInput) {
                            onDone()
                            keyboardController?.hide()
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { onFocusChanged(it.isFocused) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    cursorColor = MainColor
                )
            )

            AnimatedVisibility(visible = hasInput && !isFocused) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onPlaySourceAudio
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Translation audio"
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { clipboard.setText(AnnotatedString(sourceText)) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy text"
                        )
                    }
                }
            }
            AnimatedVisibility(visible = hasInput) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Text(
                        text = translatedText,
                        style = textStyle
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onPlayTranslationAudio) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Translation audio"
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = onToggleFavourite
                        ) {
                            Icon(
                                imageVector = if (isFavourite)
                                    Icons.Filled.Bookmark
                                else
                                    Icons.Default.BookmarkBorder,
                                tint = MainColor,
                                contentDescription = "Toggle favourite"
                            )
                        }

                        IconButton(
                            onClick = { clipboard.setText(AnnotatedString(translatedText)) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy text"
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isFocused && hasClipboardText && !hasInput) {
                PasteButton(
                    onPaste = { clipboard.getText()?.text?.let { onTextChanged(it) } }
                )
            }
        }
    }
}

@Composable
private fun rememberHasClipboardText(): Boolean {
    val context = LocalContext.current

    val clipboardManager = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    fun hasText(): Boolean {
        val description = clipboardManager.primaryClipDescription ?: return false

        return clipboardManager.hasPrimaryClip() &&
                (description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        || description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML))
    }

    var hasClipboardText by remember { mutableStateOf(hasText()) }

    DisposableEffect(clipboardManager) {
        val listener =
            ClipboardManager.OnPrimaryClipChangedListener { hasClipboardText = hasText() }

        clipboardManager.addPrimaryClipChangedListener(listener)

        onDispose {
            clipboardManager.removePrimaryClipChangedListener(listener)
        }
    }

    return hasClipboardText
}

@Composable
private fun PasteButton(onPaste: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(48.dp)
            .clickable { onPaste() },
        shape = RoundedCornerShape(16.dp),
        color = ButtonColor,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Paste"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Paste",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LanguageSelector(
    sourceLanguage: LanguageCode,
    destinationLanguage: LanguageCode,
    languageList: List<LanguageCode>,
    onSourceLanguageChange: (LanguageCode) -> Unit,
    onDestinationLanguageChange: (LanguageCode) -> Unit,
    onSwapLanguages: () -> Unit,
) {
    var isSwapping by remember { mutableStateOf(false) }

    val offset by animateDpAsState(
        targetValue = if (isSwapping) 72.dp else 0.dp,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
    )

    val scale by animateFloatAsState(
        targetValue = if (isSwapping) 0.96f else 1f,
        animationSpec = tween(durationMillis = 130)
    )

    LaunchedEffect(isSwapping) {
        if (isSwapping) {
            delay(300)

            onSwapLanguages()

            delay(130)

            isSwapping = false
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LanguageChip(
            selectedLanguage = sourceLanguage,
            languageList = languageList,
            disabledLanguage = destinationLanguage,
            onSelected = onSourceLanguageChange,
            modifier = Modifier
                .weight(1f)
                .offset(x = offset)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )

        Spacer(Modifier.width(8.dp))

        SwapButton(
            onClick = {
                if (!isSwapping && sourceLanguage.code != destinationLanguage.code)
                    isSwapping = true
            }
        )

        Spacer(Modifier.width(8.dp))

        LanguageChip(
            selectedLanguage = destinationLanguage,
            languageList = languageList,
            disabledLanguage = sourceLanguage,
            onSelected = onDestinationLanguageChange,
            modifier = Modifier
                .weight(1f)
                .offset(x = -offset)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageChip(
    selectedLanguage: LanguageCode,
    disabledLanguage: LanguageCode,
    languageList: List<LanguageCode>,
    onSelected: (LanguageCode) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable { expanded = true },
        shape = RoundedCornerShape(16.dp),
        color = TranslationBoxColor,
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = selectedLanguage.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (expanded) {
        LanguageSelectionBottomSheet(
            selectedLanguage = selectedLanguage,
            disabledLanguage = disabledLanguage,
            languageList = languageList,
            onLanguageSelected = {
                onSelected(it)
                expanded = false
            },
            onDismiss = { expanded = false }
        )
    }
}

@Composable
private fun SwapButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .size(48.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.background
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Swap languages",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BottomActionsBar(
    onHistoryNavClick: () -> Unit,
    onCameraNavClick: () -> Unit,
    isRecording: Boolean,
    onAudioButtonClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp, top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SmallActionButton(
                onClick = onHistoryNavClick,
                icon = Icons.Default.History,
                contentDescription = "History"
            )

            Spacer(Modifier.width(32.dp))

            AudioButton(isRecording = isRecording, onClick = onAudioButtonClick)

            Spacer(Modifier.width(32.dp))

            SmallActionButton(
                onClick = onCameraNavClick,
                icon = Icons.Default.CameraAlt,
                contentDescription = "Camera"
            )
        }
    }
}

@Composable
private fun SmallActionButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = ButtonColor,
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun AudioButton(isRecording: Boolean, onClick: () -> Unit) {
    val hapticFeedback = LocalHapticFeedback.current

    val activationSound = remember {
        MediaActionSound().apply {
            load(MediaActionSound.START_VIDEO_RECORDING)
            load(MediaActionSound.STOP_VIDEO_RECORDING)
        }
    }

    var isInitialState by remember { mutableStateOf(true) }

    LaunchedEffect(isRecording) {
        if (isInitialState) {
            isInitialState = false

            return@LaunchedEffect
        }

        if (isRecording)
            activationSound.play(MediaActionSound.START_VIDEO_RECORDING)
        else
            activationSound.play(MediaActionSound.STOP_VIDEO_RECORDING)

    }

    val scale by animateFloatAsState(
        targetValue = if (isRecording) 1.05f else 1f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
    )

    Surface(
        modifier = Modifier
            .size(72.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.VirtualKey)

                onClick()
            },
        shape = CircleShape,
        color = if (isRecording) MainColor else ButtonColor
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = if (isRecording) "Stop voice input" else "Voice input",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activationSound.release()
        }
    }
}

// History Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopBar(onNavBackClick: () -> Unit, onClear: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = { ButtonBack(onNavBackClick = onNavBackClick) },
        actions = {
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    tint = MainColor,
                    contentDescription = "Clear history"
                )
            }
        }
    )
}

@OptIn(ExperimentalTime::class)
@Composable
fun HistoryList(
    translationsList: List<Translation>,
    onDelete: (String) -> Unit,
    onToggleFavourite: (String, Boolean) -> Unit,
) {
    val grouped = translationsList.groupBy { formatDateTime(it.timestamp) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        grouped.forEach { (date, items) ->
            stickyHeader {
                DateHeader(date)
            }

            items(
                items = items,
                key = { it.id }
            ) { translation ->
                HistoryItemContainer(id = translation.id, onDelete = onDelete) {
                    HistoryItem(translation = translation, onToggleFavourite = onToggleFavourite)
                }
            }
        }
    }
}

@Composable
private fun HistoryItemContainer(
    id: String,
    onDelete: (String) -> Unit,
    content: @Composable () -> Unit,
) {
    @Suppress("DEPRECATION")
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete(id)
                true
            } else
                false
        }
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
            MaterialTheme.colorScheme.error
        else
            Color.Transparent
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    ) {
        Column {
            content()
            HorizontalDivider(thickness = 0.5.dp)
        }
    }
}

@Composable
private fun HistoryItem(
    translation: Translation,
    onToggleFavourite: (String, Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translation.sourceText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = translation.translatedText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { onToggleFavourite(translation.id, translation.isFavourite) }
            ) {
                Icon(
                    imageVector = if (translation.isFavourite)
                        Icons.Filled.Bookmark
                    else
                        Icons.Default.BookmarkBorder,
                    tint = MainColor,
                    contentDescription = "Toggle favourite"
                )
            }
        }
    }
}

@Composable
private fun DateHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MainColor,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

//Favourites Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesTopBar(
    onNavBackClick: () -> Unit,
    onSortByDate: () -> Unit,
    onSortByAlphabet: () -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Favourites",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = { ButtonBack(onNavBackClick = onNavBackClick) },
        actions = {
            Box {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        tint = MainColor,
                        contentDescription = "Sort Favourites"
                    )
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("By alphabet") },
                        onClick = {
                            onSortByAlphabet()
                            isMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("By date") },
                        onClick = {
                            onSortByDate()
                            isMenuExpanded = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun FavouritesList(
    translationsList: List<Translation>,
    onToggleFavourite: (String, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(
            items = translationsList,
            key = { it.id }
        ) { item ->
            FavouritesItem(
                translation = item,
                onToggleFavourite = onToggleFavourite
            )
        }
    }
}

@Composable
private fun FavouritesItem(
    translation: Translation,
    onToggleFavourite: (String, Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = translation.sourceText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = translation.translatedText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { onToggleFavourite(translation.id, translation.isFavourite) }
            ) {
                Icon(
                    imageVector = if (translation.isFavourite)
                        Icons.Filled.Bookmark
                    else
                        Icons.Default.BookmarkBorder,
                    tint = MainColor,
                    contentDescription = "Toggle favourite"
                )
            }
        }
    }

    HorizontalDivider(thickness = 0.5.dp)
}

@Composable
fun ButtonBack(onNavBackClick: () -> Unit) {
    TextButton(
        onClick = onNavBackClick,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            modifier = Modifier.size(20.dp),
            tint = MainColor,
            contentDescription = "Navigate back"
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Main screen",
            color = MainColor,
        )
    }
}

// Common Views
@Composable
fun ErrorView(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_error),
            contentDescription = "Error image",
            modifier = Modifier
                .size(width = 537.dp, height = 381.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}