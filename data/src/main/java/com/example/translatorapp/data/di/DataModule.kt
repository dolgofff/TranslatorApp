package com.example.translatorapp.data.di

import com.example.translatorapp.data.mapper.FirebaseAuthErrorMapper
import com.example.translatorapp.data.repository.AuthRepositoryImpl
import com.example.translatorapp.data.repository.TranslationRepositoryImpl
import com.example.translatorapp.domain.repository.AuthRepository
import com.example.translatorapp.domain.repository.TranslationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideErrorMapper(): FirebaseAuthErrorMapper = FirebaseAuthErrorMapper()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        errorMapper: FirebaseAuthErrorMapper,
    ): AuthRepository = AuthRepositoryImpl(firebaseAuth, errorMapper)

    @Provides
    @Singleton
    fun provideTranslationRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): TranslationRepository = TranslationRepositoryImpl(firebaseAuth, firestore)
}