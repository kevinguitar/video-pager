package com.example.videopager

import android.view.TextureView
import android.view.View

class VideoInteractor(
    private val player: VideoPlayer,
    private val index: Int,
) {

    private var textureView: TextureView? = null

    private val textureViewAttachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) = Unit
        override fun onViewDetachedFromWindow(v: View) = detachTextureView()
    }

    val playbackTrigger = MutableEventFlow<Unit>()

    fun play(textureView: TextureView) {
        setTextureView(textureView)
        player.seekTo(index)
    }

    private fun setTextureView(textureView: TextureView) {
        textureView.removeOnAttachStateChangeListener(textureViewAttachListener)
        textureView.addOnAttachStateChangeListener(textureViewAttachListener)
        player.setTextureView(textureView)
        this.textureView = textureView
    }

    private fun detachTextureView() {
        player.clearTextureView(textureView)
        textureView?.removeOnAttachStateChangeListener(textureViewAttachListener)
        textureView = null
    }
}