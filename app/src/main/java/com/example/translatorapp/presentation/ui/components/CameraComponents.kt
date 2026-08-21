package com.example.translatorapp.presentation.ui.components

import android.content.Context
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.ml.DisplayedTextBlock
import com.example.translatorapp.domain.model.ml.TextBounds
import com.example.translatorapp.presentation.screen.camera.image.ImageTranslationViewModel
import com.example.translatorapp.presentation.ui.theme.MainColor
import com.example.translatorapp.presentation.ui.theme.White
import kotlinx.coroutines.delay

// Dynamic translation
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    context: Context,
    onStartCamera: (LifecycleOwner, PreviewView) -> Unit,
    onStopCamera: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView }
    )

    DisposableEffect(lifecycleOwner, previewView) {
        onStartCamera(lifecycleOwner, previewView)

        onDispose {
            onStopCamera()
        }
    }
}

@Composable
fun CameraOverlay(blocks: List<DisplayedTextBlock>, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val previousFontSizes = remember { mutableMapOf<String, Float>() }

    Canvas(modifier = modifier) {
        blocks.forEach { block ->
            val bounds = block.bounds ?: return@forEach

            val left = bounds.left.toFloat()
            val top = bounds.top.toFloat()

            val rawWidth = (bounds.right - bounds.left).toFloat()
            val rawHeight = (bounds.bottom - bounds.top).toFloat()

            if (rawWidth <= 0f || rawHeight <= 0f || block.translatedText.isBlank())
                return@forEach

            val horizontalOuterPadding = maxOf(4f, rawWidth * 0.018f)
            val verticalOuterPadding = maxOf(2f, rawHeight * 0.06f)

            val backgroundLeft = left - horizontalOuterPadding
            val backgroundTop = top - verticalOuterPadding
            val backgroundWidth = rawWidth + horizontalOuterPadding * 2
            val backgroundHeight = rawHeight + verticalOuterPadding * 2

            val cornerRadius = minOf(backgroundHeight * 0.16f, 12f)

            drawIntoCanvas { canvas ->
                val paint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.argb(35, 0, 0, 0)

                    setShadowLayer(
                        5f,
                        0f,
                        2f,
                        android.graphics.Color.argb(45, 0, 0, 0)
                    )
                }

                canvas.nativeCanvas.drawRoundRect(
                    backgroundLeft,
                    backgroundTop,
                    backgroundLeft + backgroundWidth,
                    backgroundTop + backgroundHeight,
                    cornerRadius,
                    cornerRadius,
                    paint
                )
            }

            drawRoundRect(
                color = Color(0xFFF7F7F7).copy(alpha = 0.91f),
                topLeft = Offset(x = backgroundLeft, y = backgroundTop),
                size = Size(width = backgroundWidth, height = backgroundHeight),
                cornerRadius = CornerRadius(x = cornerRadius, y = cornerRadius)
            )

            val horizontalTextPadding = maxOf(5f, backgroundWidth * 0.018f)
            val verticalTextPadding = maxOf(2f, backgroundHeight * 0.04f)

            val availableWidth = (backgroundWidth - horizontalTextPadding * 2).toInt()
                .coerceAtLeast(1)

            val availableHeight = (backgroundHeight - verticalTextPadding * 2).toInt()
                .coerceAtLeast(1)

            val targetFontSizeSp = findBestFontSize(
                textMeasurer = textMeasurer,
                density = density,
                text = block.translatedText,
                maxWidthPx = availableWidth,
                maxHeightPx = availableHeight
            )

            val fontSizeKey = block.originalText
                .trim()
                .lowercase()

            val previousFontSize = previousFontSizes[fontSizeKey] ?: targetFontSizeSp
            val smoothedFontSize = convertFloat(
                previous = previousFontSize,
                current = targetFontSizeSp
            )

            previousFontSizes[fontSizeKey] = smoothedFontSize

            val textLayoutResult = textMeasurer.measure(
                text = block.translatedText,
                style = TextStyle(
                    color = Color.Black.copy(alpha = 0.90f),
                    fontSize = smoothedFontSize.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = (smoothedFontSize * 1.05f).sp
                ),
                constraints = Constraints(maxWidth = availableWidth),
                overflow = TextOverflow.Clip
            )

            val textLeft = backgroundLeft + horizontalTextPadding
            val textTop = backgroundTop + (backgroundHeight - textLayoutResult.size.height) / 2f

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(x = textLeft, y = textTop)
            )
        }
    }
}

private fun findBestFontSize(
    textMeasurer: TextMeasurer,
    density: Density,
    text: String,
    maxWidthPx: Int,
    maxHeightPx: Int,
): Float {
    if (text.isBlank() || maxWidthPx <= 0 || maxHeightPx <= 0)
        return 7f

    val maxFontSizeSp = with(density) {
        maxHeightPx.toFloat().toSp().value
    }
        .times(0.82f)
        .coerceAtMost(40f)

    val minFontSizeSp = 7f

    var low = minFontSizeSp
    var high = maxFontSizeSp.coerceAtLeast(minFontSizeSp)

    var best = minFontSizeSp

    repeat(8) {
        val candidate = (low + high) / 2f

        val result = textMeasurer.measure(
            text = text,
            style = TextStyle(
                fontSize = candidate.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = (candidate * 1.05f).sp
            ),
            constraints = Constraints(maxWidth = maxWidthPx),
            overflow = TextOverflow.Clip
        )

        val fits = result.size.width <= maxWidthPx && result.size.height <= maxHeightPx

        if (fits) {
            best = candidate
            low = candidate
        } else {
            high = candidate
        }
    }

    return best
}

private fun convertFloat(previous: Float, current: Float): Float =
    previous + (current - previous) * 0.25f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraTopBar(modifier: Modifier = Modifier, onNavBackClick: () -> Unit) {
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
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Translator") }
                    append(" Lens")
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = White
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
            .background(Color.Black.copy(alpha = 0.72f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50)
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransparentLanguageChip(
    language: LanguageCode,
    languageList: List<LanguageCode>,
    onSelected: (LanguageCode) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    Text(
        modifier = modifier
            .clickable { expanded = true },
        text = language.title,
        color = Color.White,
        maxLines = 1,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium
    )

    if (expanded) {
        LanguageSelectionBottomSheet(
            selectedLanguage = language,
            languageList = languageList,
            onLanguageSelected = {
                onSelected(it)
                expanded = false
            },
            onDismiss = {
                expanded = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelectionBottomSheet(
    selectedLanguage: LanguageCode,
    languageList: List<LanguageCode>,
    onLanguageSelected: (LanguageCode) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by rememberSaveable {
        mutableStateOf("")
    }

    val filteredLanguages = remember(languageList, query) {
        if (query.isBlank())
            languageList
        else
            languageList.filter {
                it.title.contains(query, ignoreCase = true)
            }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            Text(
                text = "Select language",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search language") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items = filteredLanguages, key = { it.code }) { item ->
                    LanguageItem(
                        language = item,
                        isSelected = item.code == selectedLanguage.code,
                        onClick = { onLanguageSelected(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageItem(language: LanguageCode, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = language.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CameraBottomBar(
    modifier: Modifier = Modifier,
    onCaptureClick: () -> Unit,
    onFlashClick: () -> Unit,
    onGalleryClick: () -> Unit,
    isTorchOn: Boolean,
    isCapturing: Boolean,
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
                contentDescription = "Open album",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        CaptureButton(onCaptureClick = onCaptureClick, isCapturing = isCapturing)

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
fun CaptureButton(onCaptureClick: () -> Unit, isCapturing: Boolean) {
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
                color = Color.Black.copy(alpha = if (isCapturing) 0.4f else 0.72f),
                shape = CircleShape
            )
            .clickable(enabled = !isCapturing, onClick = onCaptureClick)
    )
}

// Static translation
@Composable
fun ImageTranslationContent(
    state: ImageTranslationViewModel.ImageTranslationState,
    onNavBackClick: () -> Unit,
    onSourceLanguageChange: (LanguageCode) -> Unit,
    onDestinationLanguageChange: (LanguageCode) -> Unit,
    onSwapLanguages: () -> Unit,
    onGoToTranslatorClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        state.imageUri?.let { uri ->
            StaticTranslatedImage(
                imageUri = uri,
                imageWidth = state.imageWidth,
                imageHeight = state.imageHeight,
                blocks = state.translatedBlocks,
                modifier = Modifier.fillMaxSize()
            )
        }

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
                languageList = state.languageList,
                onSourceLanguageChange = onSourceLanguageChange,
                onDestinationLanguageChange = onDestinationLanguageChange,
                onSwapLanguages = onSwapLanguages
            )
        }

        Button(
            onClick = onGoToTranslatorClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50)
                ),
            enabled = state.recognizedText?.blocks?.isNotEmpty() == true,
            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go back to translator",
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Go to translator")
        }

        if (state.isLoading)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun StaticTranslatedImage(
    imageUri: String,
    imageWidth: Int,
    imageHeight: Int,
    blocks: List<DisplayedTextBlock>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current

        val containerWidthPx = with(density) { maxWidth.toPx() }
        val containerHeightPx = with(density) { maxHeight.toPx() }

        val transform = remember(imageWidth, imageHeight, containerWidthPx, containerHeightPx) {
            calculateFitTransform(
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                containerWidth = containerWidthPx,
                containerHeight = containerHeightPx
            )
        }

        AsyncImage(
            model = imageUri,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        StaticImageOverlay(
            blocks = blocks,
            transform = transform,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun StaticImageOverlay(
    blocks: List<DisplayedTextBlock>,
    transform: ImageFitTransform,
    modifier: Modifier = Modifier,
) {
    val transformedBlocks = remember(blocks, transform) {
        blocks.map { block ->
            val bounds = block.bounds

            if (bounds == null)
                block
            else
                block.copy(
                    bounds = TextBounds(
                        left = (bounds.left * transform.scale + transform.offsetX).toInt(),
                        top = (bounds.top * transform.scale + transform.offsetY).toInt(),
                        right = (bounds.right * transform.scale + transform.offsetX).toInt(),
                        bottom = (bounds.bottom * transform.scale + transform.offsetY).toInt()
                    )
                )
        }
    }

    CameraOverlay(blocks = transformedBlocks, modifier = modifier)
}

private fun calculateFitTransform(
    imageWidth: Int,
    imageHeight: Int,
    containerWidth: Float,
    containerHeight: Float,
): ImageFitTransform {
    if (imageWidth <= 0 || imageHeight <= 0 || containerWidth <= 0 || containerHeight <= 0)
        return ImageFitTransform(scale = 1f, offsetX = 0f, offsetY = 0f)

    val scaleX = containerWidth / imageWidth.toFloat()
    val scaleY = containerHeight / imageHeight.toFloat()

    val scale = minOf(scaleX, scaleY)

    val displayedWidth = imageWidth * scale
    val displayedHeight = imageHeight * scale

    val offsetX = (containerWidth - displayedWidth) / 2f
    val offsetY = (containerHeight - displayedHeight) / 2f

    return ImageFitTransform(scale = scale, offsetX = offsetX, offsetY = offsetY)
}

private data class ImageFitTransform(
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float,
)

