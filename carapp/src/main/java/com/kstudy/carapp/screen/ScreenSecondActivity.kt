package com.kstudy.carapp.screen

import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.kstudy.carapp.R

class ScreenSecondActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {
    private lateinit var mTextureView: TextureView
    private lateinit var mIntent: Intent
    private var width = INVALID_SIZE
    private var height = INVALID_SIZE
    private lateinit var mMediaProjectionManager: MediaProjectionManager
    private lateinit var mediaProjection: MediaProjection
    private lateinit var virtualDisplay: VirtualDisplay

    companion object {
        const val INVALID_SIZE = -1
        const val REQUEST_MEDIA_PROJECTION = 666
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dispaly_activity_second)

        mTextureView = findViewById<TextureView>(R.id.textureView)
        mTextureView.surfaceTextureListener = this

        mIntent = Intent(this, ScreenService::class.java)
        startService(mIntent)

        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK) {
            mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data!!)
            virtualDisplay = mediaProjection.createVirtualDisplay(
                "ScreenRecorder-display",
                width, height, 1,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                Surface(mTextureView.getSurfaceTexture()), null, null
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(mIntent)
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        // TODO("Not yet implemented")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        virtualDisplay.release()
        mediaProjection.stop()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        // TODO("Not yet implemented")
    }


}