package com.example.translatorapp.domain.usecase.audio

import com.example.translatorapp.domain.media.speech.AudioPlayer

class PlayAudioUseCase(private val audioPlayer: AudioPlayer) {
    operator fun invoke(url: String) {
        audioPlayer.play(url)
    }
}