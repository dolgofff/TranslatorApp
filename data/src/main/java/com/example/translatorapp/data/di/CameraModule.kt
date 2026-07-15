package com.example.translatorapp.data.di

import android.content.Context
import com.example.translatorapp.data.mapper.entity.MlTextMapper
import com.example.translatorapp.data.media.camera.CameraController
import com.example.translatorapp.data.media.camera.FrameAnalyzer
import com.example.translatorapp.data.media.camera.MLTextRecognizer
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CameraModule {
    @Provides
    @Singleton
    fun provideTextRecognizer(): TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @Provides
    @Singleton
    fun provideMLTextRecognizer(recognizer: TextRecognizer): MLTextRecognizer =
        MLTextRecognizer(recognizer)

    @Provides
    @Singleton
    fun provideCameraExecutor(): ExecutorService = Executors.newSingleThreadExecutor()

    @Provides
    @Singleton
    fun provideFrameAnalyzer(
        mlTextRecognizer: MLTextRecognizer,
        mapper: MlTextMapper,
    ): FrameAnalyzer = FrameAnalyzer(mlTextRecognizer, mapper)

    @Provides
    @Singleton
    fun provideCameraController(
        @ApplicationContext context: Context,
        frameAnalyzer: FrameAnalyzer,
        cameraExecutor: ExecutorService,
    ): CameraController = CameraController(context, frameAnalyzer, cameraExecutor)
}