package com.example.translatorapp.data.media.speech

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.translatorapp.domain.media.AudioPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@OptIn(UnstableApi::class)
class ExoAudioPlayer @Inject constructor(@ApplicationContext context: Context) : AudioPlayer {
    private val dataSourceFactory = DefaultHttpDataSource.Factory()
        .setDefaultRequestProperties(mapOf("User-Agent" to "Mozilla/5.0"))

    private val player = ExoPlayer.Builder(context).build()

    override fun play(url: String) {
        val mediaItem = MediaItem.fromUri(url)

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)

        player.apply {
            stop()
            setMediaSource(mediaSource)
            prepare()
            play()
        }
    }

    override fun stop() {
        player.stop()
        player.clearMediaItems()
    }
}