package com.example.translatorapp.data.media.camera

import android.content.Context
import android.net.Uri
import com.example.translatorapp.data.mapper.entity.MlImageTextMapper
import com.example.translatorapp.domain.model.ml.RecognizedImageText
import com.example.translatorapp.domain.model.ml.RecognizedText
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageTextRecognizer @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val textRecognizer: TextRecognizer,
    private val mlImageTextMapper: MlImageTextMapper,
) {
    suspend fun recognize(uri: Uri, sourceLanguageCode: String): Result<RecognizedImageText> =
        runCatching {
            val inputImage = InputImage.fromFilePath(context, uri)

            val text = textRecognizer
                .process(inputImage)
                .await()

            val blocks = mlImageTextMapper.map(
                text = text,
                sourceLanguageCode = sourceLanguageCode
            )

            RecognizedImageText(
                width = inputImage.width,
                height = inputImage.height,
                recognizedText = RecognizedText(blocks = blocks)
            )
        }
}
