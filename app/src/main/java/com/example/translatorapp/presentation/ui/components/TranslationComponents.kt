package com.example.translatorapp.presentation.ui.components

import android.media.MediaActionSound
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.translatorapp.R
import com.example.translatorapp.domain.model.Translation
import com.example.translatorapp.presentation.mapper.formatDateTime
import com.example.translatorapp.presentation.ui.theme.Black
import com.example.translatorapp.presentation.ui.theme.ButtonColor
import com.example.translatorapp.presentation.ui.theme.MainColor
import com.example.translatorapp.presentation.ui.theme.TranslationBoxColor
import kotlin.time.ExperimentalTime

// Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationTopBar(onFavouritesClick: () -> Unit, onAccountClick: () -> Unit) {
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
    sourceText: String,
    translatedText: String,
    isFocused: Boolean,
    hasInput: Boolean,
    onTranslationTextChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onPaste: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current
    var hasClipboardText by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val elevation by animateDpAsState(targetValue = if (isFocused) 8.dp else 2.dp)

    LaunchedEffect(isFocused) {
        if (isFocused) {
            hasClipboardText = !clipboard.getText().isNullOrEmpty()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp)
            .focusRequester(focusRequester)
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
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
                onValueChange = onTranslationTextChanged,
                placeholder = {
                    Text(
                        text = "Enter text",
                        fontSize = 30.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(560.dp)
                    .onFocusChanged {
                        onFocusChanged(it.isFocused)
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            AnimatedVisibility(visible = hasInput) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Text(
                        text = translatedText,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { /* TODO: Voice reads translation */ }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Speak"
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
                    onPaste = { clipboard.getText()?.text?.let { onPaste(it) } }
                )
            }
        }
    }
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
    sourceLanguage: String,
    targetLanguage: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LanguageChip(
            text = sourceLanguage,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(8.dp))

        SwapButton()

        Spacer(Modifier.width(8.dp))

        LanguageChip(
            text = targetLanguage,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LanguageChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        color = TranslationBoxColor,
        tonalElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SwapButton() {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .clickable { },
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
fun BottomActionsBar(onHistoryClick: () -> Unit) {
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
                onClick = onHistoryClick,
                icon = Icons.Default.History,
                contentDescription = "History"
            )

            Spacer(Modifier.width(32.dp))

            AudioButton()

            Spacer(Modifier.width(32.dp))

            SmallActionButton(
                onClick = {},
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AudioButton() {
    var isRecording by remember { mutableStateOf(false) }
    val infTranslation = rememberInfiniteTransition()

    val activationSound = remember {
        MediaActionSound().apply {
            load(MediaActionSound.START_VIDEO_RECORDING)
            load(MediaActionSound.STOP_VIDEO_RECORDING)
        }
    }

    val pulseScale by infTranslation.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        modifier = Modifier
            .size(72.dp)
            .graphicsLayer {
                scaleX = if (isRecording) pulseScale else 1f
                scaleY = if (isRecording) pulseScale else 1f
            }
            .clickable {
                isRecording = !isRecording
                if (isRecording) {
                    activationSound.play(MediaActionSound.START_VIDEO_RECORDING)
                } else {
                    activationSound.play(MediaActionSound.STOP_VIDEO_RECORDING)
                }
            },
        shape = CircleShape,
        color = ButtonColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            AnimatedContent(
                targetState = isRecording,
                transitionSpec = { (scaleIn() + fadeIn() togetherWith (scaleOut() + fadeOut())) }
            ) { recording ->
                if (recording) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop recording",
                        tint = MaterialTheme.colorScheme.onError
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice input",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
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
            IconButton(
                onClick = onClear
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    tint = Black,
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

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        grouped.forEach { (date, items) ->
            stickyHeader {
                DateHeader(date)
            }

            items(
                items = items,
                key = { it.id }
            ) { translation ->
                HistoryItemContainer(
                    id = translation.id,
                    onDelete = onDelete
                ) {
                    HistoryItem(
                        translation = translation,
                        onToggleFavourite = onToggleFavourite
                    )
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
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
            MaterialTheme.colorScheme.error
        } else {
            Color.Transparent
        }
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
                    imageVector = if (translation.isFavourite) {
                        Icons.Filled.Bookmark
                    } else {
                        Icons.Default.BookmarkBorder
                    },
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
                IconButton(
                    onClick = { isMenuExpanded = true },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
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
                    imageVector = if (translation.isFavourite) {
                        Icons.Filled.Bookmark
                    } else {
                        Icons.Default.BookmarkBorder
                    },
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
            tint = Black,
            contentDescription = "Navigate back"
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Main screen",
            color = Color.Black,
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