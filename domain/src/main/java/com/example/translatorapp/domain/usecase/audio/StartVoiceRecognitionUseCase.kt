package com.example.translatorapp.domain.usecase.audio

import com.example.translatorapp.domain.media.VoiceRecognizer
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.recognition.VoiceRecognitionEvent
import kotlinx.coroutines.flow.Flow

class StartVoiceRecognitionUseCase(private val voiceRecognizer: VoiceRecognizer) {
    operator fun invoke(languageCode: LanguageCode): Flow<VoiceRecognitionEvent> =
        voiceRecognizer.startListening(languageCode)
}