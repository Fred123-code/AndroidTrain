package com.kstudy.carapp.screen

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kstudy.carapp.R
import com.kstudy.carapp.screen.widget.MyGLRenderer
import com.kstudy.carapp.screen.widget.MyGLSurfaceView

class ScreenThirdActivity : AppCompatActivity() {
    private lateinit var mGLSurfaceView: MyGLSurfaceView
    private lateinit var mIntent: Intent
    private lateinit var mMediaProjectionManager: MediaProjectionManager
    private lateinit var mediaProjection: MediaProjection

    companion object {
        const val REQUEST_MEDIA_PROJECTION = 666
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dispaly_activity_third)

        mGLSurfaceView = findViewById(R.id.surfaceview)

        mIntent = Intent(this, ScreenService::class.java)
        startService(mIntent)

        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK) {
            mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data!!)

            val renderer: MyGLRenderer = mGLSurfaceView.getRenderer()
            renderer.setMediaProjection(mediaProjection)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(mIntent)
    }

}