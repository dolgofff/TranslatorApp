package com.example.translatorapp.presentation.di

import com.example.translatorapp.data.mapper.TranslationErrorMapperImpl
import com.example.translatorapp.domain.error.TranslationErrorMapper
import com.example.translatorapp.domain.repository.AuthRepository
import com.example.translatorapp.domain.repository.TranslationRepository
import com.example.translatorapp.domain.usecase.authorization.GetCurrentUserUseCase
import com.example.translatorapp.domain.usecase.authorization.AuthStateUseCase
import com.example.translatorapp.domain.usecase.authorization.LogOutUseCase
import com.example.translatorapp.domain.usecase.authorization.RegistrationUseCase
import com.example.translatorapp.domain.usecase.authorization.ResetPasswordUseCase
import com.example.translatorapp.domain.usecase.authorization.SignInEmailUseCase
import com.example.translatorapp.domain.usecase.authorization.SignInGoogleUseCase
import com.example.translatorapp.domain.usecase.translation.ClearHistoryUseCase
import com.example.translatorapp.domain.usecase.translation.DeleteTranslationUseCase
import com.example.translatorapp.domain.usecase.translation.ObserveFavouritesUseCase
import com.example.translatorapp.domain.usecase.translation.ObserveTranslationsUseCase
import com.example.translatorapp.domain.usecase.translation.SaveTranslationUseCase
import com.example.translatorapp.domain.usecase.translation.ToggleFavouriteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    // Authorization UseCases
    @Provides
    @Singleton
    fun provideRegistrationUseCase(authRepository: AuthRepository): RegistrationUseCase =
        RegistrationUseCase(authRepository)

    @Provides
    @Singleton
    fun provideLogOutUseCase(authRepository: AuthRepository): LogOutUseCase =
        LogOutUseCase(authRepository)

    @Provides
    @Singleton
    fun provideSignInEmailUseCase(authRepository: AuthRepository): SignInEmailUseCase =
        SignInEmailUseCase(authRepository)

    @Provides
    @Singleton
    fun provideSignInGoogleUseCase(authRepository: AuthRepository): SignInGoogleUseCase =
        SignInGoogleUseCase(authRepository)

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(authRepository: AuthRepository): GetCurrentUserUseCase =
        GetCurrentUserUseCase(authRepository)

    @Provides
    @Singleton
    fun provideObserveAuthStateUseCase(authRepository: AuthRepository): AuthStateUseCase =
        AuthStateUseCase(authRepository)

    @Provides
    @Singleton
    fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase =
        ResetPasswordUseCase(authRepository)

    // Translation UseCases
    @Provides
    @Singleton
    fun provideTranslationErrorMapper(): TranslationErrorMapper = TranslationErrorMapperImpl()

    @Provides
    @Singleton
    fun provideDeleteTranslationUseCase(
        translationRepository: TranslationRepository,
        translationErrorMapper: TranslationErrorMapper,
    ): DeleteTranslationUseCase =
        DeleteTranslationUseCase(translationRepository, translationErrorMapper)

    @Provides
    @Singleton
    fun provideSaveTranslationUseCase(
        translationRepository: TranslationRepository,
        translationErrorMapper: TranslationErrorMapper,
    ): SaveTranslationUseCase =
        SaveTranslationUseCase(translationRepository, translationErrorMapper)

    @Provides
    @Singleton
    fun provideToggleFavouriteUseCase(
        translationRepository: TranslationRepository,
        translationErrorMapper: TranslationErrorMapper,
    ): ToggleFavouriteUseCase =
        ToggleFavouriteUseCase(translationRepository, translationErrorMapper)

    @Provides
    @Singleton
    fun provideObserveTranslationsUseCase(
        translationRepository: TranslationRepository,
        translationErrorMapper: TranslationErrorMapper,
    ): ObserveTranslationsUseCase =
        ObserveTranslationsUseCase(translationRepository, translationErrorMapper)

    @Provides
    @Singleton
    fun provideObserveFavouritesUseCase(observeTranslationsUseCase: ObserveTranslationsUseCase): ObserveFavouritesUseCase =
        ObserveFavouritesUseCase(translationsFlow = observeTranslationsUseCase)

    @Provides
    @Singleton
    fun provideClearHistoryUseCase(
        translationRepository: TranslationRepository,
        translationErrorMapper: TranslationErrorMapper,
    ): ClearHistoryUseCase =
        ClearHistoryUseCase(translationRepository, translationErrorMapper)
}