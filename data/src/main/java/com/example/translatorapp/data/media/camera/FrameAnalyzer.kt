package com.example.translatorapp.data.media.camera

import android.util.Log
import androidx.camera.core.ImageProxy

class FrameAnalyzer {
    fun process(image: ImageProxy) {
        Log.d("FrameProcessor", "Frame: ${image.width}x${image.height}")

        image.close()
    }
}