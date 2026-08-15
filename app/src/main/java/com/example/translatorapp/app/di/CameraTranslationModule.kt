package com.example.translatorapp.app.di

import com.example.translatorapp.domain.media.camera.CameraTextTranslator
import com.example.translatorapp.domain.usecase.translation.TranslateTextUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object CameraTranslationModule {
    @Provides
    @ViewModelScoped
    fun provideCameraTextTranslator(translateTextUseCase: TranslateTextUseCase): CameraTextTranslator =
        CameraTextTranslator(translateTextUseCase)
}