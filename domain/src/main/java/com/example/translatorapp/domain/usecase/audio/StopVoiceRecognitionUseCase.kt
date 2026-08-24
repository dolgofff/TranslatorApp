package com.example.translatorapp.domain.usecase.audio

import com.example.translatorapp.domain.media.speech.VoiceRecognizer

class StopVoiceRecognitionUseCase(private val voiceRecognizer: VoiceRecognizer) {
    operator fun invoke() = voiceRecognizer.stopListening()
}