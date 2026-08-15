package com.example.translatorapp.data.di

import com.example.translatorapp.data.media.speech.ExoAudioPlayer
import com.example.translatorapp.domain.media.speech.AudioPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class MediaModule {
    @Binds
    @Singleton
    abstract fun bindAudioPlayer(audioPlayer: ExoAudioPlayer): AudioPlayer
}