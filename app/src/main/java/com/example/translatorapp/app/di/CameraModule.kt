package com.example.translatorapp.app.di

import com.example.translatorapp.domain.media.camera.CameraTextTranslator
import com.example.translatorapp.domain.media.camera.TextStabilizer
import com.example.translatorapp.domain.repository.RecognitionRepository
import com.example.translatorapp.domain.usecase.translation.RecognizeImageTextUseCase
import com.example.translatorapp.domain.usecase.translation.TranslateTextUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object CameraModule {
    @Provides
    @ViewModelScoped
    fun provideCameraTextTranslator(translateTextUseCase: TranslateTextUseCase): CameraTextTranslator =
        CameraTextTranslator(translateTextUseCase)

    @Provides
    @ViewModelScoped
    fun provideTextRecognitionUseCase(recognitionRepository: RecognitionRepository): RecognizeImageTextUseCase =
        RecognizeImageTextUseCase(recognitionRepository)

    @Provides
    @ViewModelScoped
    fun provideTextStabilizer(): TextStabilizer = TextStabilizer()
}