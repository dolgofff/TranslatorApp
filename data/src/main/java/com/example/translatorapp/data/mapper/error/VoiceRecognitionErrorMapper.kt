package com.example.translatorapp.data.mapper.error

import android.speech.SpeechRecognizer
import com.example.translatorapp.domain.error.VoiceRecognitionError

class VoiceRecognitionErrorMapper {
    operator fun invoke(exc: Int): VoiceRecognitionError {
        return when (exc){
            SpeechRecognizer.ERROR_NETWORK -> VoiceRecognitionError.NetworkError()
            SpeechRecognizer.ERROR_NO_MATCH -> VoiceRecognitionError.NoMatch()
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> VoiceRecognitionError.PermissionDenied()
            SpeechRecognizer.ERROR_CLIENT -> VoiceRecognitionError.ClientError()
            else -> VoiceRecognitionError.Unknown()
        }
    }
}