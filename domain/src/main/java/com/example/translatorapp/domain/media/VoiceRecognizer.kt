package com.example.translatorapp.domain.media

import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.recognition.VoiceRecognitionEvent
import kotlinx.coroutines.flow.Flow

interface VoiceRecognizer {
    fun startListening(languageCode: LanguageCode): Flow<VoiceRecognitionEvent>

    fun stopListening()
}