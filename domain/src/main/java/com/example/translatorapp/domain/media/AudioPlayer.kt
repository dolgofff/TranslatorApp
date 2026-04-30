package com.example.translatorapp.domain.media

interface AudioPlayer {
    fun play(url: String)
    fun stop()
}