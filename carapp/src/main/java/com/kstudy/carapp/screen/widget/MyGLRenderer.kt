package com.kstudy.carapp.screen.widget

import android.graphics.SurfaceTexture
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.Surface
import com.kstudy.carapp.screen.fliter.ScreenFilter
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener{
    private var surfaceView: MyGLSurfaceView
    private var mSurfaceTextName: Int? = null
    private var mSurface: Surface? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mScreenFilter: ScreenFilter? = null
    private var width = INVALID_SIZE
    private var height = INVALID_SIZE

    private var mtx = FloatArray(16)

    private var mediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null

    companion object {
        const val INVALID_SIZE = -1
    }

    constructor(surfaceView: MyGLSurfaceView) {
        this.surfaceView = surfaceView
    }

    fun setMediaProjection(mediaProjection: MediaProjection) {
        this.mediaProjection = mediaProjection
        createVirtualDisplay()
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        surfaceView.requestRender()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        var textures = IntArray(1)
        GLES20.glGenTextures(textures.size, textures, 0)
        mSurfaceTextName = textures[0]
        mSurfaceTexture = SurfaceTexture(mSurfaceTextName!!)
        mSurfaceTexture?.setOnFrameAvailableListener(this)
        mScreenFilter = ScreenFilter(context = surfaceView.context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
        createVirtualDisplay()
        mScreenFilter?.setSize(width, height)
    }

    private fun createVirtualDisplay() {
        if (mediaProjection == null ||  width == INVALID_SIZE || height == INVALID_SIZE){
            return
        }

        mSurfaceTexture?.setDefaultBufferSize(width, height)
        mSurface = Surface(mSurfaceTexture)
        mVirtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenRecorder-display",
            width, height, 1,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            mSurface, null, null
        )

    }

    override fun onDrawFrame(gl: GL10?) {
        //更新纹理
        mSurfaceTexture!!.updateTexImage()
        mSurfaceTexture!!.getTransformMatrix(mtx)

        mScreenFilter?.setTransformMatrix(mtx)
        mScreenFilter?.onDraw(mSurfaceTextName!!)
    }

    fun onSurfaceDestroyed() {
        mSurface?.release()
        mVirtualDisplay?.release()
        mediaProjection?.stop()
        width = INVALID_SIZE
        height = INVALID_SIZE
    }
}