package com.example.videopager

import android.view.SurfaceView
import android.view.View

class VideoInteractor(
    private val player: VideoPlayer,
    private val index: Int,
) {

    private var surfaceView: SurfaceView? = null

    private val surfaceViewAttachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) = Unit
        override fun onViewDetachedFromWindow(v: View) = detachSurfaceView()
    }

    val playbackTrigger = MutableEventFlow<Unit>()

    fun play(surfaceView: SurfaceView) {
        setSurfaceView(surfaceView)
        player.seekTo(index)
    }

    private fun setSurfaceView(surfaceView: SurfaceView) {
        surfaceView.removeOnAttachStateChangeListener(surfaceViewAttachListener)
        surfaceView.addOnAttachStateChangeListener(surfaceViewAttachListener)
        player.setSurfaceView(surfaceView)
        this.surfaceView = surfaceView
    }

    private fun detachSurfaceView() {
        player.clearSurfaceView(surfaceView)
        surfaceView?.removeOnAttachStateChangeListener(surfaceViewAttachListener)
        surfaceView = null
    }
}