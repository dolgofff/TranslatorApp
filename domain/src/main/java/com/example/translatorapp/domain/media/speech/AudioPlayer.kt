package com.example.translatorapp.domain.media.speech

interface AudioPlayer {
    fun play(url: String)
    fun stop()
}