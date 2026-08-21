package com.example.translatorapp.data.di


import com.example.translatorapp.data.repository.AuthRepositoryImpl
import com.example.translatorapp.data.repository.GlobalRepositoryImpl
import com.example.translatorapp.data.repository.RecognitionRepositoryImpl
import com.example.translatorapp.data.repository.TranslationRepositoryImpl
import com.example.translatorapp.data.repository.TranslatorRepositoryImpl
import com.example.translatorapp.domain.repository.AuthRepository
import com.example.translatorapp.domain.repository.GlobalRepository
import com.example.translatorapp.domain.repository.RecognitionRepository
import com.example.translatorapp.domain.repository.TranslationRepository
import com.example.translatorapp.domain.repository.TranslatorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindGlobalRepository(globalRepositoryImpl: GlobalRepositoryImpl): GlobalRepository

    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindTranslationRepository(translationRepositoryImpl: TranslationRepositoryImpl): TranslationRepository

    @Binds
    abstract fun bindTranslatorRepository(translatorRepositoryImpl: TranslatorRepositoryImpl): TranslatorRepository

    @Binds
    abstract fun bindRecognitionRepository(recognitionRepositoryImpl: RecognitionRepositoryImpl): RecognitionRepository
}