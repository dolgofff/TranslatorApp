package com.example.translatorapp.presentation.ui.components

import android.content.Context
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.ml.DisplayedTextBlock
import com.example.translatorapp.domain.model.ml.RecognizedText
import com.example.translatorapp.presentation.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    context: Context,
    onStartCamera: (LifecycleOwner, PreviewView) -> Unit,
    onStopCamera: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    LaunchedEffect(previewView, lifecycleOwner) {
        onStartCamera(lifecycleOwner, previewView)
    }

    DisposableEffect(previewView, lifecycleOwner) {
        onDispose {
            onStopCamera()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView }
    )
}

@Composable
fun CameraOverlay(
    recognizedText: RecognizedText?,
    blocks: List<DisplayedTextBlock>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        blocks.forEach { block ->
            val bounds = block.bounds ?: return@forEach

            Log.d("Overlay Bounds", "bounds=$bounds canvas=$size")

            drawRect(
                color = Color.Red,
                topLeft = Offset(
                    x = bounds.left.toFloat(),
                    y = bounds.top.toFloat()
                ),
                size = Size(
                    width = (bounds.right - bounds.left).toFloat(),
                    height = (bounds.bottom - bounds.top).toFloat()
                ),
                style = Stroke(width = 3f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraTopBar(
    modifier: Modifier = Modifier,
    onNavBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            subtitleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Text(
                text = "Translator Lens",
                style = MaterialTheme.typography.titleLarge,
                color = White,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    tint = White,
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Navigate back"
                )
            }
        }
    )
}

@Composable
fun TransparentLanguageSelector(
    modifier: Modifier = Modifier,
    sourceLanguage: LanguageCode,
    destinationLanguage: LanguageCode,
    languageList: List<LanguageCode>,
    onSourceLanguageChange: (LanguageCode) -> Unit,
    onDestinationLanguageChange: (LanguageCode) -> Unit,
    onSwapLanguages: () -> Unit,
) {
    var isSwapping by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = isSwapping)

    val offset by transition.animateDp(transitionSpec = { tween(durationMillis = 300) }) { swapping ->
        if (swapping) 40.dp else 0.dp
    }

    LaunchedEffect(isSwapping) {
        if (isSwapping) {
            delay(150)

            onSwapLanguages()

            delay(150)

            isSwapping = false
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TransparentLanguageChip(
                modifier = Modifier
                    .offset(x = offset),
                language = sourceLanguage,
                languageList = languageList,
                onSelected = { if (it.code != destinationLanguage.code) onSourceLanguageChange(it) }
            )

            Spacer(Modifier.width(4.dp))

            IconButton(
                onClick = {
                    if (!isSwapping && sourceLanguage.code != destinationLanguage.code)
                        isSwapping = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Swap languages",
                    tint = Color.White
                )
            }

            Spacer(Modifier.width(4.dp))

            TransparentLanguageChip(
                modifier = Modifier
                    .offset(x = -offset),
                language = destinationLanguage,
                languageList = languageList,
                onSelected = {
                    if (it.code != sourceLanguage.code)
                        onDestinationLanguageChange(it)
                }
            )
        }
    }
}

@Composable
private fun TransparentLanguageChip(
    language: LanguageCode,
    languageList: List<LanguageCode>,
    onSelected: (LanguageCode) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .clickable { expanded = true },
            text = language.title,
            color = Color.White,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languageList.forEach { item ->
                val isSelected = item.code == language.code

                Surface(
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        Color.Transparent

                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = {
                            onSelected(item)
                            expanded = false
                        },
                        trailingIcon = {
                            if (isSelected)
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected Language"
                                )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CameraBottomBar(
    modifier: Modifier = Modifier,
    onGalleryClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onFlashClick: () -> Unit,
    isTorchOn: Boolean,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onGalleryClick) {
            Icon(
                imageVector = Icons.Default.PhotoAlbum,
                contentDescription = "Navigate to album",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        CaptureButton(onCaptureClick = onCaptureClick)

        IconButton(onClick = onFlashClick) {
            Icon(
                imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Flash Light",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun CaptureButton(onCaptureClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(74.dp)
            .border(
                width = 4.dp,
                color = Color.White,
                shape = CircleShape
            )
            .padding(6.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .clickable(onClick = onCaptureClick)
    )
}
