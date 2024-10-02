package com.example.videopager

import android.annotation.SuppressLint
import android.content.Context
import android.view.TextureView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import kotlinx.coroutines.flow.MutableStateFlow

class VideoPlayer(context: Context) {

    private val exoPlayer = ExoPlayer.Builder(context).build()

    // test data
    private val videoUrls = listOf(
        "https://bandlab-test-video.azureedge.net/public/a21d8f46-c7c6-4350-9867-c189420feca6/original.mp4",
        /* problematic video */
        "https://bandlab-test-video.azureedge.net/public/9fc787ef-2052-410a-b001-23ff5871c775/original.mp4",
        "https://bandlab-test-video.azureedge.net/public/5e2ab6d3-87a8-4255-987c-2e2e2f34eec1/original.mp4",
        /* problematic video */
        "https://bandlab-test-video.azureedge.net/public/5c797341-14e2-45ee-a28a-1bc825f42f93/original.mp4",
        "https://bandlab-test-video.azureedge.net/public/38f7841c-74a9-4381-b308-7b3a3f191a5d/original.mp4"
    )

    val videoCount get() = videoUrls.size

    @SuppressLint("UnsafeOptInUsageError")
    val videoSize = MutableStateFlow(VideoSize.UNKNOWN)

    private val videoListener = object : Player.Listener {
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            this@VideoPlayer.videoSize.value = videoSize
        }
    }

    init {
        exoPlayer.addListener(videoListener)
        exoPlayer.addAnalyticsListener(EventLogger())

        exoPlayer.setMediaItems(
            videoUrls.map { url -> MediaItem.fromUri(url) }
        )
        exoPlayer.repeatMode = REPEAT_MODE_ONE
        exoPlayer.play()
        exoPlayer.prepare()
    }

    fun setTextureView(textureView: TextureView) {
        exoPlayer.setVideoTextureView(textureView)
    }

    fun clearTextureView(textureView: TextureView?) {
        exoPlayer.clearVideoTextureView(textureView)
    }

    fun seekTo(index: Int) {
        exoPlayer.seekTo(index, C.TIME_UNSET)
    }
}