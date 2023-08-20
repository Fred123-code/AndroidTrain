package com.kstudy.carapp.screen.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder

class MyGLSurfaceView : GLSurfaceView {
    private var mGlRenderer: MyGLRenderer? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setEGLContextClientVersion(2)
        mGlRenderer = MyGLRenderer(this)
        setRenderer(mGlRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY  // RENDERMODE_WHEN_DIRTY 按需渲染，有帧数据的时候，才会去渲染
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        mGlRenderer?.onSurfaceDestroyed();
    }

    fun getRenderer(): MyGLRenderer {
        return mGlRenderer!!
    }
}