package com.example.translatorapp.domain.model.recognition

import com.example.translatorapp.domain.error.VoiceRecognitionError

sealed interface VoiceRecognitionEvent {
    data object Listening : VoiceRecognitionEvent
    data class PartialResult(val text: String) : VoiceRecognitionEvent
    data class FinalResult(val text: String) : VoiceRecognitionEvent
    data class Error(val error: VoiceRecognitionError) : VoiceRecognitionEvent
}