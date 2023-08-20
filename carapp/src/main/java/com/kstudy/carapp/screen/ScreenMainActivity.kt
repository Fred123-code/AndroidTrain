package com.kstudy.carapp.screen

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kstudy.carapp.R

class ScreenMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dispaly_activity_main)

        findViewById<Button>(R.id.btn_together).setOnClickListener {
            val options = Bundle()
            val intent = Intent(this, ScreenThirdActivity/*或者ScreenSecondActivity*/::class.java)
            val displayManager: DisplayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val displays = displayManager.displays

            options.putInt("android.activity.launchDisplayId", displays[1].displayId)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent, options)
        }
    }


}