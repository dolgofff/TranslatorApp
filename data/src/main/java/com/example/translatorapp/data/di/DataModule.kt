package com.example.translatorapp.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.example.translatorapp.data.datastore.LanguagePreferences
import com.example.translatorapp.data.datastore.languageDataStore
import com.example.translatorapp.data.mapper.error.ApiErrorMapper
import com.example.translatorapp.data.mapper.error.FirebaseAuthErrorMapper
import com.example.translatorapp.data.mapper.error.FirebaseTranslationErrorMapper
import com.example.translatorapp.data.network.api.TranslationApi
import com.example.translatorapp.data.repository.AuthRepositoryImpl
import com.example.translatorapp.data.repository.GlobalRepositoryImpl
import com.example.translatorapp.data.repository.TranslationRepositoryImpl
import com.example.translatorapp.data.repository.TranslatorRepositoryImpl
import com.example.translatorapp.domain.repository.AuthRepository
import com.example.translatorapp.domain.repository.GlobalRepository
import com.example.translatorapp.domain.repository.TranslationRepository
import com.example.translatorapp.domain.repository.TranslatorRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<LanguagePreferences> {
        return context.languageDataStore
    }

    @Provides
    @Singleton
    fun provideGlobalRepository(dataStore: DataStore<LanguagePreferences>): GlobalRepository {
        return GlobalRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthErrorMapper(): FirebaseAuthErrorMapper = FirebaseAuthErrorMapper()

    @Provides
    @Singleton
    fun provideTranslationErrorMapper(): FirebaseTranslationErrorMapper =
        FirebaseTranslationErrorMapper()

    @Provides
    @Singleton
    fun provideApiErrorMapper(): ApiErrorMapper = ApiErrorMapper()

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
        errorMapper: FirebaseTranslationErrorMapper,
    ): TranslationRepository = TranslationRepositoryImpl(firebaseAuth, firestore, errorMapper)

    @Provides
    @Singleton
    fun provideTranslatorRepository(
        translationApi: TranslationApi,
        errorMapper: ApiErrorMapper,
    ): TranslatorRepository = TranslatorRepositoryImpl(translationApi, errorMapper)
}