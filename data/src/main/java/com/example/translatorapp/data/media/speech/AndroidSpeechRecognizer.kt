package com.example.translatorapp.data.media.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.translatorapp.data.mapper.error.VoiceRecognitionErrorMapper
import com.example.translatorapp.domain.media.speech.VoiceRecognizer
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.recognition.VoiceRecognitionEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class AndroidVoiceRecognizer(
    context: Context,
    private val errorMapper: VoiceRecognitionErrorMapper,
) : VoiceRecognizer {
    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

    override fun startListening(languageCode: LanguageCode): Flow<VoiceRecognitionEvent> =
        callbackFlow {
            val listener = object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    trySend(VoiceRecognitionEvent.Listening)
                }

                override fun onPartialResults(bundle: Bundle?) {
                    val text = bundle
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()

                    if (text != null) {
                        trySend(VoiceRecognitionEvent.PartialResult(text))
                    }
                }

                override fun onResults(bundle: Bundle?) {
                    val text = bundle
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()

                    if (text != null) {
                        trySend(VoiceRecognitionEvent.FinalResult(text))
                    }
                }

                override fun onError(error: Int) {
                    trySend(VoiceRecognitionEvent.Error(errorMapper(error)))
                }

                override fun onRmsChanged(p0: Float) = Unit

                override fun onBeginningOfSpeech() = Unit

                override fun onBufferReceived(p0: ByteArray?) = Unit

                override fun onEndOfSpeech() = Unit

                override fun onEvent(p0: Int, p1: Bundle?) = Unit
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.forLanguageTag(languageCode.code).toLanguageTag()
                )

                putExtra(
                    RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                    true
                )
            }

            recognizer.setRecognitionListener(listener)
            recognizer.startListening(intent)

            awaitClose {
                recognizer.stopListening()
                recognizer.setRecognitionListener(null)
            }
        }

    override fun stopListening() {
        recognizer.stopListening()
    }
}