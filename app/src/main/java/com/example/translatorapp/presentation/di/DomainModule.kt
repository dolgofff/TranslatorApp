package com.example.translatorapp.presentation.di

import com.example.translatorapp.data.media.ExoAudioPlayer
import com.example.translatorapp.domain.repository.AuthRepository
import com.example.translatorapp.domain.repository.GlobalRepository
import com.example.translatorapp.domain.repository.TranslationRepository
import com.example.translatorapp.domain.repository.TranslatorRepository
import com.example.translatorapp.domain.usecase.audio.PlayAudioUseCase
import com.example.translatorapp.domain.usecase.authorization.AuthStateUseCase
import com.example.translatorapp.domain.usecase.authorization.GetCurrentUserUseCase
import com.example.translatorapp.domain.usecase.authorization.LogOutUseCase
import com.example.translatorapp.domain.usecase.authorization.RegistrationUseCase
import com.example.translatorapp.domain.usecase.authorization.ResetPasswordUseCase
import com.example.translatorapp.domain.usecase.authorization.SignInEmailUseCase
import com.example.translatorapp.domain.usecase.authorization.SignInGoogleUseCase
import com.example.translatorapp.domain.usecase.dataStore.ObservePreferencesUseCase
import com.example.translatorapp.domain.usecase.dataStore.SetDestinationLanguageUseCase
import com.example.translatorapp.domain.usecase.dataStore.SetSourceLanguageUseCase
import com.example.translatorapp.domain.usecase.translation.ClearHistoryUseCase
import com.example.translatorapp.domain.usecase.translation.DeleteTranslationUseCase
import com.example.translatorapp.domain.usecase.translation.ObserveFavouritesUseCase
import com.example.translatorapp.domain.usecase.translation.ObserveTranslationsUseCase
import com.example.translatorapp.domain.usecase.translation.SaveTranslationUseCase
import com.example.translatorapp.domain.usecase.translation.ToggleFavouriteUseCase
import com.example.translatorapp.domain.usecase.translation.TranslateTextUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    // Authorization UseCases
    @Provides
    fun provideRegistrationUseCase(authRepository: AuthRepository): RegistrationUseCase =
        RegistrationUseCase(authRepository)

    @Provides
    fun provideLogOutUseCase(authRepository: AuthRepository): LogOutUseCase =
        LogOutUseCase(authRepository)

    @Provides
    fun provideSignInEmailUseCase(authRepository: AuthRepository): SignInEmailUseCase =
        SignInEmailUseCase(authRepository)

    @Provides
    fun provideSignInGoogleUseCase(authRepository: AuthRepository): SignInGoogleUseCase =
        SignInGoogleUseCase(authRepository)

    @Provides
    fun provideGetCurrentUserUseCase(authRepository: AuthRepository): GetCurrentUserUseCase =
        GetCurrentUserUseCase(authRepository)

    @Provides
    fun provideObserveAuthStateUseCase(authRepository: AuthRepository): AuthStateUseCase =
        AuthStateUseCase(authRepository)

    @Provides
    fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase =
        ResetPasswordUseCase(authRepository)

    // Translation UseCases
    @Provides
    fun provideDeleteTranslationUseCase(translationRepository: TranslationRepository): DeleteTranslationUseCase =
        DeleteTranslationUseCase(translationRepository)

    @Provides
    fun provideSaveTranslationUseCase(translationRepository: TranslationRepository): SaveTranslationUseCase =
        SaveTranslationUseCase(translationRepository)

    @Provides
    fun provideToggleFavouriteUseCase(translationRepository: TranslationRepository): ToggleFavouriteUseCase =
        ToggleFavouriteUseCase(translationRepository)

    @Provides
    fun provideObserveTranslationsUseCase(translationRepository: TranslationRepository): ObserveTranslationsUseCase =
        ObserveTranslationsUseCase(translationRepository)

    @Provides
    fun provideObserveFavouritesUseCase(observeTranslationsUseCase: ObserveTranslationsUseCase): ObserveFavouritesUseCase =
        ObserveFavouritesUseCase(translationsFlow = observeTranslationsUseCase)

    @Provides
    fun provideClearHistoryUseCase(translationRepository: TranslationRepository): ClearHistoryUseCase =
        ClearHistoryUseCase(translationRepository)

    @Provides
    fun provideTranslateTextUseCase(translatorRepository: TranslatorRepository): TranslateTextUseCase =
        TranslateTextUseCase(translatorRepository)

    // Persistent data UseCases
    @Provides
    fun provideSetSourceLanguageUseCase(globalRepository: GlobalRepository): SetSourceLanguageUseCase =
        SetSourceLanguageUseCase(globalRepository)

    @Provides
    fun provideSetDestinationLanguageUseCase(globalRepository: GlobalRepository): SetDestinationLanguageUseCase =
        SetDestinationLanguageUseCase(globalRepository)

    @Provides
    fun provideObservePreferencesUseCase(globalRepository: GlobalRepository): ObservePreferencesUseCase =
        ObservePreferencesUseCase(globalRepository)

    // Media UseCases
    @Provides
    fun providePlayAudioUseCase(audioPlayer: ExoAudioPlayer): PlayAudioUseCase =
        PlayAudioUseCase(audioPlayer)

}