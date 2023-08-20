package com.kstudy.monitor.fps

import android.view.Choreographer

object ChoreographerHelper {
    fun start() {
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            var lastFrameTimeNanos: Long = 0

            override fun doFrame(frameTimeNanos: Long) {
                // 上次回调时间
                if (lastFrameTimeNanos.toInt() == 0) {
                    lastFrameTimeNanos = frameTimeNanos
                    Choreographer.getInstance().postFrameCallback(this)
                    return
                }

                val diff = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000
                if (diff > 16.6f) {
                    val droppedCount = (diff / 16.6).toInt()
                    //TODO: upload droppedCount implement
                    if (diff > 40.0f)
                    println("&&&&&droppedCount=$droppedCount,diff=$diff")
                }

                lastFrameTimeNanos = frameTimeNanos
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }
}