package com.example.translatorapp.domain.error

sealed class VoiceRecognitionError : Exception() {
    class PermissionDenied() : VoiceRecognitionError()
    class NetworkError() : VoiceRecognitionError()
    class NoMatch() : VoiceRecognitionError()
    class ClientError() : VoiceRecognitionError()
    class Unknown() : VoiceRecognitionError()
}